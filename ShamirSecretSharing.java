import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ShamirSecretSharing {

    public static void main(String[] args) {
        try {
            // Load and parse JSON data
            JSONObject jsonObject1 = new JSONObject(new JSONTokener(new FileInputStream("testcase1.json")));
            BigInteger constantTerm1 = findConstantTermFromJson(jsonObject1);
            System.out.println("Constant term for first test case: " + constantTerm1);

            JSONObject jsonObject2 = new JSONObject(new JSONTokener(new FileInputStream("testcase2.json")));
            BigInteger constantTerm2 = findConstantTermFromJson(jsonObject2);
            System.out.println("Constant term for second test case: " + constantTerm2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static BigInteger findConstantTermFromJson(JSONObject jsonObject) {
        // Read n and k values
        JSONObject keys = jsonObject.getJSONObject("keys");
        int n = keys.getInt("n");
        int k = keys.getInt("k");

        // Collect points (x, y)
        List<Point> points = new ArrayList<>();
        for (String key : jsonObject.keySet()) {
            if (!key.equals("keys")) {
                int x = Integer.parseInt(key);
                JSONObject pointData = jsonObject.getJSONObject(key);
                int base = pointData.getInt("base");
                String valueStr = pointData.getString("value");
                BigInteger y = new BigInteger(valueStr, base);
                points.add(new Point(x, y));
            }
        }

        // Ensure we have at least k points and use only the first k points
        if (points.size() < k) {
            throw new IllegalArgumentException("Not enough points provided.");
        }
        points = points.subList(0, k);

        // Compute f(0) using Lagrange interpolation
        return lagrangeInterpolation(points, BigInteger.ZERO);
    }

    private static BigInteger lagrangeInterpolation(List<Point> points, BigInteger xValue) {
        BigInteger result = BigInteger.ZERO;
        int k = points.size();

        for (int i = 0; i < k; i++) {
            BigInteger xi = BigInteger.valueOf(points.get(i).x);
            BigInteger yi = points.get(i).y;

            BigInteger term = yi;
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    BigInteger xj = BigInteger.valueOf(points.get(j).x);
                    term = term.multiply(xValue.subtract(xj)).divide(xi.subtract(xj));
                }
            }
            result = result.add(term);
        }
        return result;
    }

    // Helper class to store points
    static class Point {
        int x;
        BigInteger y;

        Point(int x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }
}

