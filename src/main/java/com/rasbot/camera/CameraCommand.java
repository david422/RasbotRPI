package com.rasbot.camera;

/**
 * Created by dawidpodolak on 06.08.2016.
 */
public class CameraCommand {


    private int fps;

    private int rot;

    private int heightRes;

    private long videoLength;

    private int widthRes;

    private String host;

    private int port;

    private boolean horizontalFlip;

    private boolean verticalFlip;

    private long bitRate;

    public int getFps() {
        return fps;
    }

    private void setFps(int fps) {
        this.fps = fps;
    }

    public int getRot() {
        return rot;
    }

    private void setRot(int rot) {
        this.rot = rot;
    }

    public int getHeightRes() {
        return heightRes;
    }

    private void setHeightRes(int heightRes) {
        this.heightRes = heightRes;
    }

    public long getVideoLength() {
        return videoLength;
    }

    private void setVideoLength(long videoLength) {
        this.videoLength = videoLength;
    }

    public int getWidthRes() {
        return widthRes;
    }

    private void setWidthRes(int widthRes) {
        this.widthRes = widthRes;
    }

    public String getHost() {
        return host;
    }

    private void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    private void setPort(int port) {
        this.port = port;
    }

    public boolean isHorizontalFlip() {
        return horizontalFlip;
    }

    private void setHorizontalFlip(boolean horizontalFlip) {
        this.horizontalFlip = horizontalFlip;
    }

    public boolean isVerticalFlip() {
        return verticalFlip;
    }

    private void setVerticalFlip(boolean verticalFlip) {
        this.verticalFlip = verticalFlip;
    }

    public long getBitRate() {
        return bitRate;
    }

    private void setBitRate(long bitRate) {
        this.bitRate = bitRate;
    }

    public String get(){
        StringBuilder commandBuilder = new StringBuilder();

        commandBuilder.append("nohup raspivid ");

        if (rot > 0){
            commandBuilder.append("-rot ").append(rot).append(" ");
        }

        commandBuilder.append("-t ").append(videoLength).append(" ");
        commandBuilder.append("-h ").append(heightRes).append(" ");
        commandBuilder.append("-w ").append(widthRes).append(" ");
        commandBuilder.append("-fps ").append(fps).append(" ");

        if (horizontalFlip){
            commandBuilder.append("-hf ");
        }

        if (verticalFlip){
            commandBuilder.append("-vf ");
        }

        commandBuilder.append("-b ").append(bitRate).append(" ");

        commandBuilder.append("-n -o - | gst-launch-1.0 -v fdsrc ! h264parse ! rtph264pay config-interval=1 pt=96 ! gdppay ! tcpserversink ");

        commandBuilder.append("host=").append(host).append(" ");
        commandBuilder.append("port=").append(port).append(" ");
        commandBuilder.append("&");

        return commandBuilder.toString();
    }

    private CameraCommand(){}

    public static class CameraCommandBuilder{
//Command to run camera with proper values
//"nohup raspivid -rot 180 -t 0 -h 360 -w 640 -fps 25 -hf -vf -b 2000000 -o - | gst-launch-1.0 -v fdsrc ! h264parse ! rtph264pay config-interval=1 pt=96 ! gdppay ! tcpserversink host 192.168.2.1 port 8554"


        private CameraCommand cameraCommand;

        public CameraCommandBuilder() {
            this.cameraCommand = new CameraCommand();
            setFps(25);
            setVerticalFlip(true);
            setHorizontalFlip(true);
            setVideoLength(0);
            setBitrate(2000000000);
            setRotation(180);
            setHeighRes(360);
            setWidthRes(640);
            setHost("192.168.2.1");
            setPort(8554);
        }

        public CameraCommandBuilder setFps(int fps){
            cameraCommand.setFps(fps);
            return this;
        };

        public CameraCommandBuilder setRotation(int rot){
            cameraCommand.setRot(rot);
            return this;
        }

        public CameraCommandBuilder setHeighRes(int height){
            cameraCommand.setHeightRes(height);
            return this;
        }

        public CameraCommandBuilder setWidthRes(int width){
            cameraCommand.setWidthRes(width);
            return this;
        }

        public CameraCommandBuilder setBitrate(long bitrate){
            cameraCommand.setBitRate(bitrate);
            return this;
        }

        public CameraCommandBuilder setVideoLength(long videoLength){
            cameraCommand.setVideoLength(videoLength);
            return this;
        }

        public CameraCommandBuilder setHorizontalFlip(boolean horizontalFlip){
            cameraCommand.setHorizontalFlip(horizontalFlip);
            return this;
        }

        public CameraCommandBuilder setVerticalFlip(boolean verticalFlip){
            cameraCommand.setVerticalFlip(verticalFlip);
            return this;
        }

        public CameraCommandBuilder setHost(String host){
            cameraCommand.setHost(host);
            return this;
        }

        public CameraCommandBuilder setPort(int port){
            cameraCommand.setPort(port);
            return this;
        }

        public CameraCommand build(){
            return cameraCommand;
        }
    }



}
