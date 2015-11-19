package com.tysci.applibrary.networks;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.IOException;

import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 *
 * Created by Administrator on 2015/11/10.
 */
public class ProgressRequestBody extends RequestBody{
    protected RequestBody delegate;
    protected ProgressListener listener;

    protected ProgressSink countingSink;

    public ProgressRequestBody(RequestBody delegate, ProgressListener listener)
    {
        this.delegate = delegate;
        this.listener = listener;
    }

    @Override
    public MediaType contentType()
    {
        return delegate.contentType();
    }

    @Override
    public long contentLength()
    {
        try
        {
            return delegate.contentLength();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException
    {
        BufferedSink bufferedSink;

        countingSink = new ProgressSink(sink);
        bufferedSink = Okio.buffer(countingSink);

        delegate.writeTo(bufferedSink);

        bufferedSink.flush();
    }

    protected final class ProgressSink extends ForwardingSink
    {

        private long bytesWritten = 0;

        public ProgressSink(Sink delegate)
        {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException
        {
            super.write(source, byteCount);

            bytesWritten += byteCount;
            listener.onRequestProgress(bytesWritten, contentLength());
        }

    }

    public static interface ProgressListener
    {

        public void onRequestProgress(long bytesWritten, long contentLength);

    }
}
