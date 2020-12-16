package io.alerium.discordwhitelist.cache;

import java.util.Locale;
import java.util.SplittableRandom;

@SuppressWarnings("SpellCheckingInspection")
public final class CodeBuilder {

    private static final SplittableRandom RANDOM = new SplittableRandom();

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE_ALPHABET = ALPHABET.toLowerCase(Locale.ROOT);
    private static final String NUMERAL = "1234567890";

    private static final String ALPHANUM = ALPHABET + NUMERAL + LOWERCASE_ALPHABET;

    /**
     * Constructs and returns a random String code
     * with the desired size
     *
     * @param size  The size of returned code
     * @return      A random String Code
     */
    private static String getRandomCode(final String codeType, final int size) {
        final String codeContent = CodeType.valueOfNotNullable(codeType).content;
        String result = "";

        for (int i = 0; i < size; i++) {
            result = result.concat(String.valueOf(codeContent.charAt(RANDOM.nextInt(codeContent.length() - 1))));
        }

        return result;
    }

    /**
     * Constructs and returns a random String code
     * with the desired size
     *
     * @return      A random String code
     */
    public static String getRandomCode(final int length, final String codeType) {
        return getRandomCode(codeType, length);
    }

    private enum CodeType {

        ALPHABET(CodeBuilder.ALPHABET),
        NUMERIC(CodeBuilder.NUMERAL),
        ALPHANUMERIC(CodeBuilder.ALPHANUM);

        final String content;

        CodeType(final String content) {
            this.content = content;
        }

        private static CodeType valueOfNotNullable(final String codeType) {
            CodeType result = null;

            for (final CodeType type : values()) {
                if (type.name().equalsIgnoreCase(codeType)) {
                    result = type;
                    break;
                }
            }

            return result != null ? result : CodeType.ALPHANUMERIC;
        }

    }

}
