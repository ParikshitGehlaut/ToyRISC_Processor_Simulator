package processor.memorysystem;

public class CacheLine {
    private int[] data;
    private int[] tag;

    public CacheLine(int[] data, int[] tag) {
        this.data = data;
        this.tag = tag;
    }

    public int[] getData() {
        return data;
    }

    public void setData(int dataValue, int address) {
        if (address >= 0 && address < data.length) {
            this.data[address] = dataValue;
        } else {
            throw new IllegalArgumentException("Invalid memory address: " + address);
        }
    }

    public int[] getTag() {
        return tag;
    }

    public void setTag(int tagValue, int address) {
        if (address >= 0 && address < tag.length) {
            this.tag[address] = tagValue;
        } else {
            throw new IllegalArgumentException("Invalid memory address: " + address);
        }

    }
}
