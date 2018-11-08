package com.plus.Command;

public class StereOnwithCDCommand implements Command {
    Stereo stereo;

    public StereOnwithCDCommand(Stereo stereo){
        this.stereo=stereo;
    }

    public void execute(){
        stereo.on();
        stereo.setCD();
        stereo.setVolume(11);
    }
}
