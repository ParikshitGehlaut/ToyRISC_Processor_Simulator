package processor.memorysystem;

import processor.Clock;

public class CacheLine {
    int data;
    int tag;
    long lru_value;

    public CacheLine() {
        this.tag = -1;
        this.data = -1;
        this.lru_value = Clock.getCurrentTime();
    }

    public void setData(int newData) {
        this.lru_value = Clock.getCurrentTime();
        this.data = newData;
    }

    public int getData() {
        this.lru_value = Clock.getCurrentTime();
        return this.data;
    }

    public void setTag(int newTag) {
        this.tag = newTag;
    }

    public int getTag() {
        return this.tag;
    }

}
