package com.fortytwo.fixme.common;

import com.fortytwo.fixme.router.MessageType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/*
**  This class will contain some utils functions, they will be used by the Router to decode FIX messages.
*/
public class Utils {
    public static String[] getPairs(String message) {
        return message.split("\\|");
    }

    ///  ACK message should only contain one pair, for instance, an ack message should only contain 35=121212.
    public static boolean isAckFixMessage(String[] pairs) {
        return pairs.length != 1;
    }

    /// Tag 54 is used to indicate a message type, it's either buy for 1 or sell for 2
    public static MessageType getRequestType(String[] pairs) {
        for (String pair : pairs) {
            String[] pairSplit = pair.split("=");
            if (pairSplit[0].equals("54")) {
                if (pairSplit[1].length() == 1 && pairSplit[1].equals("1")) {
                    return MessageType.BUY;
                } else if  (pairSplit[1].length() == 1 && pairSplit[1].equals("2")) {
                    return MessageType.SELL;
                }
            }
        }
        return MessageType.UNKNOWN;
    }

    // TODO: implement isFixMessageValid
    /// checks if the message is a set composed of key-value pairs
    public static boolean isFixMessageValid(String[] pairs) {
        return true;
    }

    /// we are assuming that the function is valid, and it contains the checksum tag number 10
    public static String getChecksumPairValue(String[] pairs) throws InvalidChecksumException {
        boolean found = false;
        for (String pair : pairs) {
            String[] pairSplit = pair.split("=");
            if (pairSplit[0].equals("10")) {
                found = true;
                break;
            }
        }

        if (!found) {
            throw new InvalidChecksumException("Checksum pair value not found");
        }

        for (String pair : pairs) {
            String[] pairSplit = pair.split("=");
            if (pairSplit[0].equals("10") && pairSplit[1].matches("^\\d{6}$")) {
                return pairSplit[1];
            } else {
                throw new InvalidChecksumException("Invalid checksum pair value: " + pair);
            }
        }

        return "";
    }

    ///  the Router will use this function to validate the checksum
    public boolean isChecksumValid(int value, int messageLength) {
        return value == messageLength % 256;
    }

    ///  Brokers & Markets will use this function to generate the checksum
    public String getChecksum(int len) {
        return Integer.toString(len % 256);
    }

    /// Takes in a config file that contains Instruments and returns a list.
    public LinkedList<Instrument> getInstruments(String path) {
        LinkedList<Instrument> instruments = new LinkedList<>();
        String name;
        String company;
        String sector;
        int price, quantity;
        boolean isTraded;

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#"))
                    continue;
                String[] parts = line.split(",");
                if (parts.length != 6) {
                    System.err.println("Invalid config line: " + line);
                    continue;
                }
                name = parts[0].trim();
                company = parts[1].trim();
                sector = parts[2].trim();
                price = Integer.parseInt(parts[3].trim());
                quantity = Integer.parseInt(parts[4].trim());
                isTraded = Boolean.parseBoolean(parts[5].trim());
                instruments.add(new Instrument(name, company, sector, price, quantity, isTraded));
            }
        } catch (IOException e) {
            System.out.println("Exception occurred " + e.getMessage());
        }

        return instruments;
    }
}
