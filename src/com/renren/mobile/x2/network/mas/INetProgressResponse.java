package com.renren.mobile.x2.network.mas;

/**
 * User: afpro
 * Date: 11-12-26
 * Time: 下午5:06
 */
public interface INetProgressResponse extends INetResponse {
    public void uploadContentLength(long bytes);
    public void upload(int bytes);

    public void downloadContentLength(long bytes);
    public void download(int bytes);
}
