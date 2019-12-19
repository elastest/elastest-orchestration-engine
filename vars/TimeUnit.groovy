enum TimeUnit {

    MILLISECONDS("MILLISECONDS"),
    SECONDS("SECONDS"),
    MINUTES("MINUTES"),
    HOURS("HOURS")

    String value;

    private TimeUnit(String value) {
        this.value = value
    }

    public long convertToMillis(time) {
        switch(this.value) {
            case "SECONDS":
                return time * 1000
                break;
            case "MINUTES":
                return time * 60 * 1000
                break;
            case "HOURS":
                break;
            case "MILLISECONDS":
                return time * 60 * 60 * 1000
            default:
                return time;
        }
    }
}
