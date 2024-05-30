package modelInterface;

public abstract class Camera {
    protected double cameraX, cameraY;
    protected double cameraRad, sinRad, cosRad;
    protected double transformX, transformY, transformAngle, transformZoom;

    public void addAngle(double angleDegree){
        transformAngle += angleDegree;
    }

    public double getCameraX() {
        return this.cameraX;
    }

    public void setCameraX(double cameraX) {
        this.cameraX = cameraX;
    }

    public double getCameraY() {
        return this.cameraY;
    }

    public void setCameraY(double cameraY) {
        this.cameraY = cameraY;
    }

    public double getCameraRad() {
        return this.cameraRad;
    }

    public void setCameraRad(double cameraRad) {
        this.cameraRad = cameraRad;
    }

    public double getSinRad() {
        return this.sinRad;
    }

    public void setSinRad(double sinRad) {
        this.sinRad = sinRad;
    }

    public double getCosRad() {
        return this.cosRad;
    }

    public void setCosRad(double cosRad) {
        this.cosRad = cosRad;
    }

    public double getTransformX() {
        return this.transformX;
    }

    public void setTransformX(double transformX) {
        this.transformX = transformX;
    }

    public double getTransformY() {
        return this.transformY;
    }

    public void setTransformY(double transformY) {
        this.transformY = transformY;
    }

    public double getTransformAngle() {
        return this.transformAngle;
    }

    public void setTransformAngle(double transformAngle) {
        this.transformAngle = transformAngle;
    }

    // Update sine and cosine of tranforming angle
    public void setSinCosRad(double cameraAngle){
        cameraRad = Math.toRadians(cameraAngle);
        sinRad = Math.sin(cameraRad);
        cosRad = Math.cos(cameraRad);
    }

    public double getTransformZoom() {
        return this.transformZoom;
    }

    public void setTransformZoom(double transformZoom) {
        this.transformZoom = transformZoom;
    }

    public abstract void initialize();
}
