package Function;

public class Combination {

    private final int M = 2013;
    int[] ff = new int[M + 5];  //打表，记录n!，避免重复计算

    //求最大公因数
    private int gcd(int a, int b) {
        if (b == 0)
            return a;
        else
            return gcd(b, a % b);
    }

    //解线性同余方程，扩展欧几里德定理
    int x, y;

    private void Extended_gcd(int a, int b) {
        if (b == 0) {
            x = 1;
            y = 0;
        } else {
            Extended_gcd(b, a % b);
            long t = x;
            x = y;
            y = (int) (t - (a / b) * y);
        }
    }

    //计算不大的C(n,m)
    private int C(int a, int b) {
        if (b > a)
            return 0;
        b = (ff[a - b] * ff[b]) % M;
        a = ff[a];
        int c = gcd(a, b);
        a /= c;
        b /= c;
        Extended_gcd(b, M);
        x = (x + M) % M;
        x = (x * a) % M;
        return x;
    }

    //Lucas定理
    public int getCombination(int n, int m) {
        int ans = 1;
        int a, b;
        while (m > 0 || n > 0) {
            a = n % M;
            b = m % M;
            n /= M;
            m /= M;
            ans = (ans * C(a, b)) % M;
        }
        return ans;
    }

    public static double getC3(int m,int n){
        int  i;
        if(n>m-n) n=m-n;
        double s1=0.0;
        double s2=0.0;
        for (int j = m-n+1; j <=m; j++) {
            s1+=Math.log(j);
        }
        for (int j = 1; j <=n; j++) {
            s2+=Math.log(j);
        }
        return Math.exp(s1-s2);
    }

//    public static long getC2(double n, int k) {
//        BigInteger c = new BigInteger("1");
//        if (k > n / 2) {
//            k = (int) (n - k);
//        }
//        for (int i = 1; i <= k; i++) {
//            c.multiply((n + 1 - i))
//            c *= (n + 1 - i);
//            c /= i;
//        }
//        return c;
//    }

    public static long getC(double n, int k) {
        long a = 1, b = 1;
        if (k > n / 2) {
            k = (int) (n - k);
        }
        for (int i = 1; i <= k; i++) {
            a *= (n + 1 - i);
            b *= i;
        }
        return a / b;
    }


}
