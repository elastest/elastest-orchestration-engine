enum Compare {
    LessThan {
        @Override
        public boolean eval(val1, val2) {
            return val1 < val2
        }
    },
    GreaterThan {
        @Override
        public boolean eval(val1, val2) {
            return val1 > val2
        }
    }


    public abstract boolean eval(val1, val2);
}
