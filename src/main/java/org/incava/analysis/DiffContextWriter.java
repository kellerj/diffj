package org.incava.analysis;

import java.awt.Point;
import java.io.*;
import java.util.*;
import org.incava.ijdk.io.*;
import org.incava.ijdk.lang.*;
import org.incava.qualog.Qualog;
import org.incava.ijdk.util.ANSI;


/**
 * Writes differences with context. Actually returns the differences as strings.
 * Writing is left to the invoker.
 */
public class DiffContextWriter extends DiffWriter {
    /**
     * The end-of-line character/sequence for this OS.
     */
    protected final static String EOLN = FileExt.EOLN;

    /**
     * The color for added code.
     */
    protected static String COLOR_ADDED = ANSI.YELLOW;

    /**
     * The color for deleted code.
     */
    protected static String COLOR_DELETED = ANSI.RED;

    public DiffContextWriter(String[] fromContents, String[] toContents) {
        super(fromContents, toContents);
    }

    protected void printFrom(StringBuilder sb, FileDiff ref) {
        printLines(sb, true, ref, ref.firstStart.x, ref.firstStart.y, ref.firstEnd.x, ref.firstEnd.y, fromContents);
    }

    protected void printTo(StringBuilder sb, FileDiff ref) {
        printLines(sb, false, ref, ref.secondStart.x, ref.secondStart.y, ref.secondEnd.x, ref.secondEnd.y, toContents);
    }

    protected String getLine(String[] lines, int lidx, int fromLine, int fromColumn, int toLine, int toColumn, boolean isDelete) {
        StringBuilder sb = new StringBuilder();

        sb.append("! ").append(lines[lidx - 1]).append(EOLN);

        return sb.toString();
    }

    protected void printLines(StringBuilder sb, boolean isDelete, FileDiff ref, int fromLine, int fromColumn, int toLine, int toColumn, String[] lines) {
        for (int lnum = Math.max(0, fromLine - 4); lnum < fromLine - 1; ++lnum) {
            sb.append("  ").append(lines[lnum]);
            sb.append(EOLN);
        }

        // PMD reports columns using tabSize == 8, so we replace tabs with
        // spaces here.
        // ... I loathe tabs.

        for (int lidx = fromLine; lidx <= toLine; ++lidx) {
            String line = getLine(lines, lidx, fromLine, fromColumn, toLine, toColumn, isDelete);
            sb.append(line);
        }

        for (int lnum = toLine; lnum < Math.min(toLine + 3, lines.length); ++lnum) {
            sb.append("  ").append(lines[lnum]);
            sb.append(EOLN);
        }
    }

    // protected void printLines(StringBuilder sb, Point pt, String[] lines) {
    //     for (int lnum = Math.max(0, pt.x - 4); lnum < pt.x - 1; ++lnum) {
    //         sb.append("  " + lines[lnum]);
    //         sb.append(EOLN);
    //     }

    //     // point is 1-indexed, lines are 0-indexed
    //     for (int lnum = pt.x - 1; lnum < pt.y; ++lnum) {
    //         sb.append(" !" + lines[lnum]);
    //         sb.append(EOLN);
    //     }

    //     for (int lnum = pt.y; lnum < Math.min(pt.y + 3, lines.length); ++lnum) {
    //         sb.append("  " + lines[lnum]);
    //         sb.append(EOLN);
    //     }
    // }

    protected void printLines(StringBuilder sb, FileDiff ref) {
        ref.print(this, sb);
    }
}
