package com.renren.mobile.x2.network.talk.binder;

/**
 * @author dingwei.chen
 */
abstract class LoopThread extends Thread {

    public abstract void loop() throws Exception;

    public abstract void onException(Exception e);


    @Override
    public void run() {
        while (true) {
            try {
                loop();
            } catch (Exception e) {
                this.onException(e);
            }
        }
    }

}
