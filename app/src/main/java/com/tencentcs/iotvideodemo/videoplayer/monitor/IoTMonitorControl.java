package com.tencentcs.iotvideodemo.videoplayer.monitor;

public interface IoTMonitorControl {
    void setMonitorOwner(MonitorPlayerOwner owner);

    void backClicked();

    void snapClicked();

    void recordClicked(boolean on);

    void talkClicked(boolean on);

    void muteClicked(boolean on);

    enum Definition {
        LD, SD, HD
    }

    void definitionClicked(Definition definition);

    enum Direction {
        Top, Right, Bottom, Left, None
    }

    void directionClicked(boolean isStart, Direction direction);

    enum Mode {
        ShowDirectCtl, ShowPopupMenu, Playback, Alarm, Infrared, WhiteLight, Light, Sensor, PresetPoint,
        MultiCall
    }

    void modeClicked(Mode mode);
}
