package taskone;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

class StringList {
    
    List<String> strings = new ArrayList<String>();

    public void add(String str) {
        int pos = strings.indexOf(str);
        if (pos < 0) {
            strings.add(str);
        }
    }

    public void clear() {
        strings.clear();
    }

    public boolean contains(String str) {
        return strings.indexOf(str) >= 0;
    }

    public int size() {
        return strings.size();
    }

    public String get(int index) {
        return strings.get(index);
    }

    public String toString() {
        return strings.toString();
    }

    public String sort() {
        Collections.sort(strings);
        return strings.toString();
    }

    public void set(int index, String str){
        strings.set(index, str);
    }
}