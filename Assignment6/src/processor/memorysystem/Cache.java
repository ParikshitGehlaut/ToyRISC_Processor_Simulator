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
    int[] data;
    int[] tag;
    CacheLine cacheLine;
    int cacheMissAddress;
    Element cacheMissElement;
    boolean load_request = false;
    int StoreData;

    public Cache(Processor containingProcessor, int size) {
        this.containingProcessor = containingProcessor;
        this.cacheSize = size;
        // Line Size is given 4B
        this.numberOfLines = (cacheSize / 4);
        // Initialize data and tag array
        this.data = new int[numberOfLines];
        this.tag = new int[numberOfLines];

        if (cacheSize == 16) {
            cacheLatency = 1;
        } else if (cacheSize == 128) {
            cacheLatency = 2;
        } else if (cacheSize == 512) {
            cacheLatency = 3;
        } else if (cacheSize == 1024) {
            cacheLatency = 4;
        }

        for (int i = 0; i < numberOfLines; i++) {
            data[i] = -1;
            tag[i] = -1;
        }

        cacheLine = new CacheLine(data, tag);
    }

    public int getCacheLatency() {
        return cacheLatency;
    }

    public static String intToBinaryString(int x, int len) {
        // String binaryString = Integer.toBinaryString(x);
        // int padding = len - binaryString.length();
        // if (padding > 0) {
        // return String.format("%0" + len + "d", Integer.parseInt(binaryString));
        // } else {
        // return binaryString;
        // }
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
        int cacheTag = cacheLine.getTag()[cacheAddress];
        if (cacheTag == address) {
            Simulator.getEventQueue().addEvent(
                    new MemoryResponseEvent(
                            Clock.getCurrentTime(),
                            this,
                            requestingElement,
                            cacheLine.getData()[cacheAddress]

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
        int cacheTag = cacheLine.getTag()[cacheAddress];
        if (cacheTag == address) {
            cacheLine.setData(data, cacheAddress);
            Simulator.getEventQueue().addEvent(
                    new MemoryWriteEvent(
                            Clock.getCurrentTime(),
                            this,
                            containingProcessor.getMainMemory(),
                            address,
                            data));
            ((MemoryAccess) requestingElement).EX_MA_Latch.setMA_Busy(false);
            ((MemoryAccess) requestingElement).MA_RW_Latch.setRW_enable(true);
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
        cacheLine.setData(data, cacheAddress);
        cacheLine.setTag(cacheMissAddress, cacheAddress);

        if (load_request) {
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

    @Override
    public void handleEvent(Event e) {
        if (e.getEventType() == Event.EventType.MemoryResponse) {
            MemoryResponseEvent event = (MemoryResponseEvent) e;
            handleResponse(event.getValue());
        } else if (e.getEventType() == Event.EventType.MemoryRead) {
            MemoryReadEvent event = (MemoryReadEvent) e;
            cacheRead(event.getAddressToReadFrom(), event.getRequestingElement());
        }

        else if (e.getEventType() == Event.EventType.MemoryWrite) {
            MemoryWriteEvent event = (MemoryWriteEvent) e;
            cacheWrite(event.getAddressToWriteTo(), event.getValue(), event.getRequestingElement());
        }
    }
}