package tec.codescanner.compiler;

import java.util.Objects;

public class Count {

    private String string;
    private long count = 1;
    private long loss = 0;

    private int byteRange = 2;

    public Count(String string) {
        this.string = string;
        setLoss();
    }

    private void setLoss() {
        loss = (count * string.length()) - ((byteRange + 1) * count) - (string.length() + (byteRange + 1));
    }

    public void increment() {
        count++;
        setLoss();
    }

    public long getCount() {
        return count;
    }

    public long getLoss() {
        setLoss();
        return loss;
    }

    public void setByteRange(int count) {
        int b = 0;
        while (Math.pow(16, b) + 1 <= count) {
            b++;
        }
        byteRange = b;
        if (byteRange > 256) {
            byteRange = 256;
        }
    }

    public int getByteRange() {
        return byteRange;
    }

    @Override
    public String toString() {
        return "Count{" +
                "string='" + string + '\'' +
                ", count=" + count +
                ", loss=" + loss +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Count)) return false;
        Count count = (Count) o;
        return string.equals(count.string);
    }

    @Override
    public int hashCode() {
        return Objects.hash(string);
    }

}
