package net.mfritsch.ucsmilleniumfalcon;

public class Falcon {

    private boolean SublightDrives;
    private Weapons Weapons;
    public boolean WeaponsUp;
    public boolean WeaponsLow;
    private boolean ForwardFloodights;
    private boolean CockpitLights;
    private boolean CockpitFittings;
    private boolean MainHold;
    private boolean BoardingRamp;
    private Light Light;
    private boolean LandingGear;
    private boolean LandingLight;

    public boolean getSublightDrivers() {
        return SublightDrives;
    }

    public void setSublightDrivers(boolean sublightDrives) {
        this.SublightDrives = sublightDrives;
    }

    public Weapons getWeapons() {
        return Weapons;
    }

    public void setWeapons(Falcon.Weapons weapons) {
        this.Weapons = weapons;
    }

    public boolean getWeaponsUp() {

        return WeaponsUp;
    }

    public void setWeaponsUp(boolean weaponsUp) {
        this.WeaponsUp = weaponsUp;
    }

    public boolean getWeaponsLow() {
        return WeaponsLow;
    }

    public void setWeaponsLow(boolean weaponsLow) {
        this.WeaponsLow = weaponsLow;
    }

    public class Weapons {
        public boolean WeaponsUp = true;
        public boolean WeaponsLow = true;

        public boolean getWeaponsUp() {
            return WeaponsUp;
        }

        public void setWeaponsUp(boolean weaponsUp) {
            this.WeaponsUp = weaponsUp;
        }

        public boolean getWeaponsLow() {
            return WeaponsLow;
        }

        public void setWeaponsLow(boolean weaponsLow) {
            this.WeaponsLow = weaponsLow;
        }
    }

    public boolean getForwardFloodights() {
        return ForwardFloodights;
    }

    public void setForwardFloodights(boolean forwardFloodights) {
        this.ForwardFloodights = forwardFloodights;
    }

    public boolean getCockpitLights() {
        return CockpitLights;
    }

    public void setCockpitLights(boolean cockpitLights) {
        this.CockpitLights = cockpitLights;
    }

    public boolean getCockpitFittings() {
        return CockpitFittings;
    }

    public void setCockpitFittings(boolean cockpitFittings) {
        this.CockpitFittings = cockpitFittings;
    }

    public boolean getMainHold() {
        return MainHold;
    }

    public void setMainHold(boolean mainHold) {
        this.MainHold = mainHold;
    }

    public boolean getBoardingRamp() {
        return BoardingRamp;
    }

    public void setBoardingRamp(boolean boardingRamp) {
        this.BoardingRamp = boardingRamp;
    }

    public Falcon.Light getLight() {
        return Light;
    }

    public void setLight(Falcon.Light light) {
        this.Light = light;
    }

    public boolean getLandingGear() {
        return LandingGear;
    }

    public void setLandingGear(boolean landingGear) {
        this.LandingGear = landingGear;
    }

    public boolean getLandingLight() {
        return LandingLight;
    }

    public void setLandingLight(boolean landingLight) {
        this.LandingLight = landingLight;
    }

    public class Light {
        private boolean LandingGear;
        private boolean LandingLight;

        public boolean getLandingGear() {
            return LandingGear;
        }

        public void setLandingGear(boolean landingGear) {
            this.LandingGear = landingGear;
        }

        public boolean getLandingLight() {
            return LandingLight;
        }

        public void setLandingLight(boolean landingLight) {
            this.LandingLight = landingLight;
        }
    }
}