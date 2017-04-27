package jianqiang.com.mylibrary.utils;

/**
 * Created by jianqiang on 16/11/21.
 */
public class Utils {

    public static int sum(int a, int b) {
        return a + b;
    }

    public static String UrlEncodeUnicode(final String s)
    {
        if (s == null)
        {
            return null;
        }
        final int length = s.length();
        final StringBuilder builder = new StringBuilder(length); // buffer
        for (int i = 0; i < length; i++)
        {
            final char ch = s.charAt(i);
            if ((ch & 0xff80) == 0)
            {
                if (Utils.IsSafe(ch))
                {
                    builder.append(ch);
                }
                else if (ch == ' ')
                {
                    builder.append('+');
                }
                else
                {
                    builder.append('%');
                    builder.append(Utils.IntToHex((ch >> 4) & 15));
                    builder.append(Utils.IntToHex(ch & 15));
                }
            }
            else
            {
                builder.append("%u");
                builder.append(Utils.IntToHex((ch >> 12) & 15));
                builder.append(Utils.IntToHex((ch >> 8) & 15));
                builder.append(Utils.IntToHex((ch >> 4) & 15));
                builder.append(Utils.IntToHex(ch & 15));
            }
        }
        return builder.toString();
    }

    static char IntToHex(final int n)
    {
        if (n <= 9)
        {
            return (char) (n + 0x30);
        }
        return (char) ((n - 10) + 0x61);
    }

    static boolean IsSafe(final char ch)
    {
        if ((((ch >= 'a') && (ch <= 'z')) || ((ch >= 'A') && (ch <= 'Z'))) || ((ch >= '0') && (ch <= '9')))
        {
            return true;
        }
        switch (ch)
        {
            case '\'':
            case '(':
            case ')':
            case '*':
            case '-':
            case '.':
            case '_':
            case '!':
                return true;
        }
        return false;
    }
}
