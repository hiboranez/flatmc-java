package Base;

public class StringConversion {

    // 将int[][]转化为String
    public static String intDoubleArrayToString(int[][] array) {
        StringBuilder result = new StringBuilder();

        for (int[] row : array) {
            for (int num : row) {
                result.append(num).append("&");
            }
            // 每行结束时换行
            result.append("#");
        }

        return result.toString();
    }

    // 将String转换回int[][]
    public static int[][] stringToIntDoubleArray(String str) {
        // 按行分割字符串
        String[] rows = str.trim().split("#");
        int numRows = rows.length;

        // 按列分割每一行
        String[] cols = rows[0].trim().split("&");
        int numCols = cols.length;

        // 初始化二维数组
        int[][] result = new int[numRows][numCols];

        // 填充二维数组
        for (int i = 0; i < numRows; i++) {
            cols = rows[i].trim().split("&");
            for (int j = 0; j < numCols; j++) {
                result[i][j] = Integer.parseInt(cols[j]);
            }
        }

        return result;
    }

    public static String intArrayToString(int[] array) {
        StringBuilder result = new StringBuilder();

        for (int num : array) {
            result.append(num).append("&");
        }

        // 删除最后一个"&"
        if (result.length() > 0) {
            result.deleteCharAt(result.length() - 1);
        }

        return result.toString();
    }

    public static int[] stringToIntArray(String str) {
        // 按"&"分割字符串
        String[] nums = str.trim().split("&");
        int length = nums.length;

        // 初始化数组
        int[] result = new int[length];

        // 填充数组
        for (int i = 0; i < length; i++) {
            result[i] = Integer.parseInt(nums[i]);
        }

        return result;
    }

}
