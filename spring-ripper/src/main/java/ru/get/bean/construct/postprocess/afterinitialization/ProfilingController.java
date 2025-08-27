package ru.get.bean.construct.postprocess.afterinitialization;

public class ProfilingController implements ProfilingControllerMBean {
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    private boolean enable;
}
