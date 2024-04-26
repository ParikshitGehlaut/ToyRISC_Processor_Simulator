package processor.memorysystem;

import configuration.Configuration;
import generic.Element;
import generic.Event;
import generic.MemoryReadEvent;
import generic.MemoryResponseEvent;
import generic.MemoryWriteEvent;
import generic.Simulator;
import processor.Clock;
import processor.Processor;
import processor.pipeline.MemoryAccess;

public class Cache implements Element {
    Processor containingProcessor;
    int cacheSize;
    int cacheLatency;
    int numberOfLines;
    CacheLine[] cache;
    int cacheMissAddress;
    Element cacheMissElement;
    boolean load_request = false;
    int StoreData;

    public Cache(Processor containingProcessor, int size) {
        this.containingProcessor = containingProcessor;
        this.cacheSize = size;
        // Line Size is given 4B
        this.numberOfLines = (cacheSize / 4);
        // Initialize data and tag array of size equals to numberofLines in the Cache

        if (cacheSize == 16) {
            cacheLatency = 1;
        } else if (cacheSize == 128) {
            cacheLatency = 2;
        } else if (cacheSize == 512) {
            cacheLatency = 3;
        } else if (cacheSize == 1024) {
            cacheLatency = 4;
        }

        cache = new CacheLine[numberOfLines];
        for (int i = 0; i < numberOfLines; i++) {
            cache[i] = new CacheLine();
        }
    }

    public int getCacheLatency() {
        return cacheLatency;
    }

    public static String intToBinaryString(int x, int len) {
        if (len > 0) {
            return String.format("%" + len + "s",
                    Integer.toBinaryString(x)).replace(" ", "0");
        }
        return null;
    }

    public void cacheRead(int address, Element requestingElement) {
        String addressBinaryString = intToBinaryString(address, 32);
        int indexBits = (int) (Math.log(numberOfLines) / Math.log(2));
        int cacheAddress;
        if (indexBits == 0) {
            cacheAddress = 0;
        } else {
            assert addressBinaryString != null;
            cacheAddress = Integer.parseInt(addressBinaryString.substring((32 - indexBits), 32), 2);
        }
        int cacheTag = cache[cacheAddress].getTag();
        if (cacheTag == address) {
            Simulator.getEventQueue().addEvent(
                    new MemoryResponseEvent(
                            Clock.getCurrentTime(),
                            this,
                            requestingElement,
                            cache[cacheAddress].getData()

                    ));
        } else {
            // handle cache miss
            load_request = true;
            handleCacheMiss(address, requestingElement);
        }

    }

    public void cacheWrite(int address, int data, Element requestingElement) {
        String addressBinaryString = intToBinaryString(address, 32);
        int indexBits = (int) (Math.log(numberOfLines) / Math.log(2));
        int cacheAddress;
        if (indexBits == 0) {
            cacheAddress = 0;
        } else {
            assert addressBinaryString != null;
            cacheAddress = Integer.parseInt(addressBinaryString.substring((32 - indexBits), 32), 2);
        }
        int cacheTag = cache[cacheAddress].getTag();
        if (cacheTag == address) {
            cache[cacheAddress].setData(data);
            Simulator.getEventQueue().addEvent(
                    new MemoryWriteEvent(
                            Clock.getCurrentTime(),
                            this,
                            containingProcessor.getMainMemory(),
                            address,
                            data));
            ((MemoryAccess) requestingElement).MA_RW_Latch.setRW_enable(true);
            ((MemoryAccess) requestingElement).EX_MA_Latch.setMA_Busy(false);
        } else {
            // handle miss cache here
            StoreData = data;
            load_request = false;
            handleCacheMiss(address, requestingElement);
        }

    }

    public void handleResponse(int data) {
        String addressBinaryString = intToBinaryString(cacheMissAddress, 32);
        int indexBits = (int) (Math.log(numberOfLines) / Math.log(2));
        int cacheAddress;
        if (indexBits == 0) {
            cacheAddress = 0;
        } else {
            assert addressBinaryString != null;
            cacheAddress = Integer.parseInt(addressBinaryString.substring((32 - indexBits), 32), 2);
        }
        cache[cacheAddress].setData(data);
        cache[cacheAddress].setTag(cacheMissAddress);

        if (load_request) {
            evictLRUElement();
            // if it is load request(read) that load data to the requesting element
            Simulator.getEventQueue().addEvent(
                    new MemoryResponseEvent(
                            Clock.getCurrentTime(),
                            this,
                            cacheMissElement,
                            data));
        } else {
            // if it is store request then call cacheWrite
            cacheWrite(cacheMissAddress, StoreData, cacheMissElement);
        }
    }

    public void handleCacheMiss(int address, Element requestingElement) {
        Simulator.getEventQueue().addEvent(
                new MemoryReadEvent(
                        Clock.getCurrentTime() + Configuration.mainMemoryLatency,
                        this,
                        containingProcessor.getMainMemory(),
                        address));
        cacheMissAddress = address;
        cacheMissElement = requestingElement;
    }

    public void evictLRUElement() {
        long minLRUValue = Long.MAX_VALUE;
        int minLRUIndex = -1;

        for (int i = 0; i < cache.length; i++) {
            if (cache[i].lru_value < minLRUValue) {
                minLRUValue = cache[i].lru_value;
                minLRUIndex = i;
            }
        }

        if (minLRUIndex != -1) {
            cache[minLRUIndex] = new CacheLine();
        }
    }

    @Override
    public void handleEvent(Event e) {
        if (e.getEventType() == Event.EventType.MemoryResponse) {
            MemoryResponseEvent event = (MemoryResponseEvent) e;
            handleResponse(event.getValue());
        } else if (e.getEventType() == Event.EventType.MemoryWrite) {
            MemoryWriteEvent event = (MemoryWriteEvent) e;
            cacheWrite(event.getAddressToWriteTo(), event.getValue(), event.getRequestingElement());
        } else if (e.getEventType() == Event.EventType.MemoryRead) {
            MemoryReadEvent event = (MemoryReadEvent) e;
            cacheRead(event.getAddressToReadFrom(), event.getRequestingElement());
        }
    }
}