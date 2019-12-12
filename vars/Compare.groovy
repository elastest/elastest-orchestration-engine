enum Compare {

    LessThan("LessThan"),
    GreaterThan("GreaterThan")

    String value;

    private Compare(String value) {
        this.value = value
    }

    public eval(val1, val2) {
        if("GreaterThan".equals(this.value)) {
            return val1 > val2;
        } else {
            return val1 < val2;
        }
    }
}
