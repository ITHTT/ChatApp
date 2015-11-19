package com.tysci.chatapp.messages;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.rong.common.FileUtils;
import io.rong.common.RLog;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.utils.BitmapUtil;

/**
 * Created by Administrator on 2015/11/17.
 */
public class VideoMessageHandler extends MessageContent.MessageHandler<MideaVideoMessage>{

    private static int COMPRESSED_SIZE = 960;
    private static int COMPRESSED_QUALITY = 85;

    private static int THUMB_COMPRESSED_SIZE = 480;
    private static int THUMB_COMPRESSED_QUALITY = 80;
    private final static String IMAGE_LOCAL_PATH = "/image/local/";
    private final static String IMAGE_THUMBNAIL_PATH = "/image/thumbnail/";

    public VideoMessageHandler(Context context) {
        super(context);
    }

    @Override
    public void afterDecodeMessage(Message message, MideaVideoMessage model) {
        if (message != null) {
            Uri uri = obtainImageUri(getContext());
            String name = message.getMessageId() + ".jpg";

            String thumb = uri.toString() + IMAGE_THUMBNAIL_PATH;
            String local = uri.toString() + IMAGE_LOCAL_PATH;

            File localFile = new File(local + name);
            if(localFile.exists()) {
                model.setLocalUri(Uri.parse("file://" + local + name));
            }

            File thumbFile = new File(thumb + name);

            if (!TextUtils.isEmpty(model.getBase64()) && !thumbFile.exists()) {
                byte[] data = null;
                try {
                    data = Base64.decode(model.getBase64(), Base64.NO_WRAP);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    RLog.e(this, "afterDecodeMessage", "Not Base64 Content!");
                    return;
                }

                if(!isImageFile(data)) {
                    RLog.e(this, "afterDecodeMessage", "Not Image File!");
                    return;
                }
                FileUtils.byte2File(data, thumb, name);
            }
            model.setThumUri(Uri.parse("file://" + thumb + name));
        }

        model.setBase64(null);
    }

    @Override
    public boolean beforeEncodeMessage(Message message, MideaVideoMessage model) {

        Uri uri = obtainImageUri(getContext());
        String name = message.getMessageId() + ".jpg";

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Resources resources = getContext().getResources();
        try {
            COMPRESSED_QUALITY = resources.getInteger(resources.getIdentifier("rc_image_quality", "integer", getContext().getPackageName()));
            COMPRESSED_SIZE = resources.getInteger(resources.getIdentifier("rc_image_size", "integer", getContext().getPackageName()));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        if (model.getThumUri() != null
                && model.getThumUri().getScheme() != null
                && model.getThumUri().getScheme().equals("file")) {

            byte[] data;
            File file = new File(uri.toString() + IMAGE_THUMBNAIL_PATH + name);
            if(file.exists()) {
                model.setThumUri(Uri.parse("file://" + uri.toString() + IMAGE_THUMBNAIL_PATH + name));
                data = FileUtils.file2byte(file);
                if(data != null)
                    model.setBase64(Base64.encodeToString(data, Base64.NO_WRAP));
            } else {
                try {
                    String thumbPath = model.getThumUri().toString().substring(5);
                    RLog.d(this, "beforeEncodeMessage", "Thumbnail not save yet! " + thumbPath);
                    BitmapFactory.decodeFile(thumbPath, options);
                    if (options.outWidth > THUMB_COMPRESSED_SIZE || options.outHeight > THUMB_COMPRESSED_SIZE) {
                        Bitmap bitmap = BitmapUtil.getResizedBitmap(getContext(),
                                model.getThumUri(),
                                THUMB_COMPRESSED_SIZE,
                                THUMB_COMPRESSED_SIZE);
                        if (bitmap != null) {
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, THUMB_COMPRESSED_QUALITY, outputStream);
                            data = outputStream.toByteArray();
                            model.setBase64(Base64.encodeToString(data, Base64.NO_WRAP));
                            outputStream.close();
                            FileUtils.byte2File(data, uri.toString() + IMAGE_THUMBNAIL_PATH, name);
                            model.setThumUri(Uri.parse("file://" + uri.toString() + IMAGE_THUMBNAIL_PATH + name));
                        }
                    } else {
                        File src = new File(thumbPath);
                        data = FileUtils.file2byte(src);
                        if(data != null) {
                            model.setBase64(Base64.encodeToString(data, Base64.NO_WRAP));
                            String path = uri.toString() + IMAGE_THUMBNAIL_PATH;
                            if ((FileUtils.copyFile(src, path, name)) != null) {
                                model.setThumUri(Uri.parse("file://" + path + name));
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if(model.getLocalUri() != null
                && model.getLocalUri().getScheme() != null
                && model.getLocalUri().getScheme().equals("file")) {

            File file = new File(uri.toString() + IMAGE_LOCAL_PATH + name);
            if (file.exists()) {
                model.setLocalUri(Uri.parse("file://" + uri.toString() + IMAGE_LOCAL_PATH + name));
            } else {
                try {
                    String localPath = model.getLocalUri().toString().substring(5);
                    BitmapFactory.decodeFile(localPath, options);
                    if (options.outWidth > COMPRESSED_SIZE || options.outHeight > COMPRESSED_SIZE && !model.isFull()) {
                        Bitmap bitmap = BitmapUtil.getResizedBitmap(getContext(),
                                model.getLocalUri(),
                                COMPRESSED_SIZE,
                                COMPRESSED_SIZE);
                        if (bitmap != null) {
                            String dir = uri.toString() + IMAGE_LOCAL_PATH;
                            file = new File(dir);
                            if(!file.exists())
                                file.mkdirs();
                            file = new File(dir + name);

                            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                            bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSED_QUALITY, bos);
                            bos.close();
                            model.setLocalUri(Uri.parse("file://" + dir + name));
                        }
                    } else {
                        if ((FileUtils.copyFile(new File(localPath), uri.toString() + IMAGE_LOCAL_PATH, name)) != null) {
                            model.setLocalUri(Uri.parse("file://" + uri.toString() + IMAGE_LOCAL_PATH + name));
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return true;
    }

    private static Uri obtainImageUri(Context context) {
        File file = context.getFilesDir();
        String path = file.getAbsolutePath();
        String userId = PreferenceManager.getDefaultSharedPreferences(context).getString("userId", "");
        Uri uri = Uri.parse(path + File.separator + userId);
        return uri;
    }

    private static boolean isImageFile(byte[] data) {
        if(data == null || data.length == 0)
            return false;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        if (options.outWidth == -1) {
            return false;
        }
        return true;
    }
}
