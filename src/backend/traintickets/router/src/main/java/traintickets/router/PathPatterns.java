package traintickets.router;

import java.util.regex.Pattern;

final class PathPatterns {
    static final Pattern nonParametrized = Pattern.compile("^[a-zA-Z0-9-_]+$");
    static final Pattern both = Pattern.compile("^(\\*|:?[a-zA-Z0-9-_]+)$");
}
