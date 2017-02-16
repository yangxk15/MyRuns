package edu.dartmouth.cs.xiankai_yang.myruns.model;

/**
 * Created by yangxk15 on 2/15/17.
 */

public class WekaClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifier.N17602b9b0(i);
        return p;
    }
    static double N17602b9b0(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 96.721751) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() > 96.721751) {
            p = WekaClassifier.N591cef0c1(i);
        }
        return p;
    }
    static double N591cef0c1(Object []i) {
        double p = Double.NaN;
        if (i[64] == null) {
            p = 1;
        } else if (((Double) i[64]).doubleValue() <= 10.428959) {
            p = WekaClassifier.N500f7db62(i);
        } else if (((Double) i[64]).doubleValue() > 10.428959) {
            p = 2;
        }
        return p;
    }
    static double N500f7db62(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 131.242683) {
            p = WekaClassifier.N73caad173(i);
        } else if (((Double) i[0]).doubleValue() > 131.242683) {
            p = 1;
        }
        return p;
    }
    static double N73caad173(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 1;
        } else if (((Double) i[7]).doubleValue() <= 2.109521) {
            p = 1;
        } else if (((Double) i[7]).doubleValue() > 2.109521) {
            p = WekaClassifier.N65607c064(i);
        }
        return p;
    }
    static double N65607c064(Object []i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 0;
        } else if (((Double) i[8]).doubleValue() <= 3.008792) {
            p = 0;
        } else if (((Double) i[8]).doubleValue() > 3.008792) {
            p = 1;
        }
        return p;
    }
}
