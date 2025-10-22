package com.fortytwo.fixme.common;

import com.fortytwo.fixme.router.MessageType;

/*
**  This class will contain some utils functions, they will be used by the Router to decode FIX messages.
*/
public class FixMessage {
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
}
