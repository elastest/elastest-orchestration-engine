enum TimeUnit {

    MILLISECONDS {
        @Override
        public long convertToMillis(time) {
            return time
        }
    },
    SECONDS{
        @Override
        public long convertToMillis(time) {
            return time * 1000
        }
    },
    MINUTES{
        @Override
        public long convertToMillis(time) {
            return time * 60 * 1000
        }
    },
    HOURS{
        @Override
        public long convertToMillis(time) {
            return time * 60 * 60 * 1000
        }
    }


    public abstract long convertToMillis(time);
}
