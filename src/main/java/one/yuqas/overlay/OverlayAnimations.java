package one.yuqas.overlay;

public final class OverlayAnimations {

    private OverlayAnimations() {}

    public enum Mode {
        LINEAR,

        EASE_IN_QUAD,
        EASE_OUT_QUAD,
        EASE_IN_OUT_QUAD,

        EASE_IN_CUBIC,
        EASE_OUT_CUBIC,
        EASE_IN_OUT_CUBIC,

        EASE_IN_QUART,
        EASE_OUT_QUART,
        EASE_IN_OUT_QUART,

        EASE_IN_EXPO,
        EASE_OUT_EXPO,

        EASE_OUT_BACK,
        EASE_OUT_BOUNCE
    }

    public static float linear(float t) {
        return t;
    }

    public static float easeInQuad(float t) {
        return t * t;
    }

    public static float easeOutQuad(float t) {
        return 1 - (1 - t) * (1 - t);
    }

    public static float easeInOutQuad(float t) {
        return t < 0.5f
                ? 2 * t * t
                : 1 - (float)Math.pow(-2 * t + 2, 2) / 2;
    }

    public static float easeInCubic(float t) {
        return t * t * t;
    }

    public static float easeOutCubic(float t) {
        return 1 - (float)Math.pow(1 - t, 3);
    }

    public static float easeInOutCubic(float t) {
        return t < 0.5f
                ? 4 * t * t * t
                : 1 - (float)Math.pow(-2 * t + 2, 3) / 2;
    }

    public static float easeInQuart(float t) {
        return t * t * t * t;
    }

    public static float easeOutQuart(float t) {
        return 1 - (float)Math.pow(1 - t, 4);
    }

    public static float easeInOutQuart(float t) {
        return t < 0.5f
                ? 8 * t * t * t * t
                : 1 - (float)Math.pow(-2 * t + 2, 4) / 2;
    }

    public static float easeOutExpo(float t) {
        return t == 1f ? 1f : 1f - (float)Math.pow(2, -10 * t);
    }

    public static float easeInExpo(float t) {
        return t == 0f ? 0f : (float)Math.pow(2, 10 * t - 10);
    }

    public static float easeOutBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1f;
        return 1 + c3 * (float)Math.pow(t - 1, 3)
                + c1 * (float)Math.pow(t - 1, 2);
    }

    public static float easeOutBounce(float t) {
        float n1 = 7.5625f;
        float d1 = 2.75f;

        if (t < 1 / d1) {
            return n1 * t * t;
        } else if (t < 2 / d1) {
            t -= 1.5f / d1;
            return n1 * t * t + 0.75f;
        } else if (t < 2.5 / d1) {
            t -= 2.25f / d1;
            return n1 * t * t + 0.9375f;
        } else {
            t -= 2.625f / d1;
            return n1 * t * t + 0.984375f;
        }
    }

    public static float apply(Mode mode, float t) {
        return switch (mode) {
            case LINEAR -> linear(t);

            case EASE_IN_QUAD -> easeInQuad(t);
            case EASE_OUT_QUAD -> easeOutQuad(t);
            case EASE_IN_OUT_QUAD -> easeInOutQuad(t);

            case EASE_IN_CUBIC -> easeInCubic(t);
            case EASE_OUT_CUBIC -> easeOutCubic(t);
            case EASE_IN_OUT_CUBIC -> easeInOutCubic(t);

            case EASE_IN_QUART -> easeInQuart(t);
            case EASE_OUT_QUART -> easeOutQuart(t);
            case EASE_IN_OUT_QUART -> easeInOutQuart(t);

            case EASE_IN_EXPO -> easeInExpo(t);
            case EASE_OUT_EXPO -> easeOutExpo(t);

            case EASE_OUT_BACK -> easeOutBack(t);
            case EASE_OUT_BOUNCE -> easeOutBounce(t);
        };
    }
}