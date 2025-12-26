import java.util.ArrayList;
import java.util.List;

public class Dictionary<K, V> {

    // Inner class for holding data
    class Entry<K, V> {
        K key;
        V val;
        boolean active; // check if deleted

        Entry(K k, V v) {
            this.key = k;
            this.val = v;
            this.active = true;
        }
    }

    private Entry<K, V>[] table;
    private int cap;
    private int size;
    private int q;        // prime for double hashing
    private double lf;    // load factor
    private String hType; // SSF or PAF
    private String cType; // LP or DH
    private long cols = 0;

    public Dictionary(int cap, double lf, String hType, String cType) {
        this.cap = cap;
        this.lf = lf;
        this.hType = hType;
        this.cType = cType;
        // casting array
        this.table = (Entry<K, V>[]) new Entry[cap];
        this.size = 0;
        updateQ();
    }

    public void put(K key, V val) {
        // check if table is full
        double rate = (double) size / cap;
        if (rate >= lf) {
            resize();
        }

        int idx = find(key, true);

        // if no place found, resize and try again
        if (idx == -1) {
            resize();
            idx = find(key, true);
        }

        if (idx == -1) {
            return;
        }

        if (table[idx] == null) {
            table[idx] = new Entry<>(key, val);
            size++;
        } else {
            // if spot is deleted, reuse it
            if (table[idx].active == false) {
                table[idx] = new Entry<>(key, val);
                size++;
            } else {
                // update value
                table[idx].val = val;
            }
        }
    }

    public V get(K key) {
        int idx = find(key, false);
        if (idx != -1) {
            if (table[idx] != null) {
                if (table[idx].active) {
                    return table[idx].val;
                }
            }
        }
        return null;
    }

    public List<K> keys() {
        List<K> list = new ArrayList<>();
        for (int i = 0; i < cap; i++) {
            if (table[i] != null) {
                if (table[i].active) {
                    list.add(table[i].key);
                }
            }
        }
        return list;
    }

    private void resize() {
        Entry<K, V>[] old = table;

        // double size and find prime
        cap = nextPrime(cap * 2);

        table = (Entry<K, V>[]) new Entry[cap];
        size = 0;
        cols = 0;
        updateQ();

        // readd old items
        for (int i = 0; i < old.length; i++) {
            if (old[i] != null) {
                if (old[i].active) {
                    put(old[i].key, old[i].val);
                }
            }
        }
    }

    private int nextPrime(int n) {
        if (n % 2 == 0) {
            n++;
        }
        while (true) {
            if (isPrime(n)) {
                break;
            }
            n += 2;
        }
        return n;
    }

    private boolean isPrime(int n) {
        if (n <= 1) {
            return false;
        }
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    private int find(K key, boolean count) {
        int h = hash(key);
        int idx = h;
        int i = 0;
        int dk = 1;

        // double hashing
        if (cType.equals("DH")) {
            long raw = 0;
            if (hType.equals("PAF")) {
                raw = paf(key.toString());
            } else {
                raw = ssf(key.toString());
            }

            int mod = (int) (Math.abs(raw) % q);
            dk = q - mod;
        }

        while (table[idx] != null) {
            // count collisions
            if (count) {
                if (table[idx].active) {
                    if (!table[idx].key.equals(key)) {
                        cols++;
                    }
                }
            }

            // found key
            if (table[idx].active) {
                if (table[idx].key.equals(key)) {
                    return idx;
                }
            }

            // found empty spot
            if (count) {
                if (table[idx].active == false) {
                    break;
                }
            }

            i++;

            // next index calculation
            if (cType.equals("LP")) {
                idx = (h + i) % cap;
            } else {
                idx = (h + (i * dk)) % cap;
                if (idx < 0) {
                    idx += cap;
                }
            }

            if (i > cap) {
                return -1;
            }
        }
        return idx;
    }

    private int hash(K key) {
        long res = 0;
        if (hType.equals("PAF")) {
            res = paf(key.toString());
        } else {
            res = ssf(key.toString());
        }
        return (int) (Math.abs(res) % cap);
    }

    private long ssf(String str) {
        long sum = 0;
        for (int i = 0; i < str.length(); i++) {
            sum += str.charAt(i);
        }
        return sum;
    }

    private long paf(String str) {
        long z = 33;
        long res = 0;
        long limit = 1000000007L; // big prime number

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            int val = c;

            // convert letters
            if (c >= 'a' && c <= 'z') {
                val = c - 'a' + 1;
            } else if (c >= 'A' && c <= 'Z') {
                val = c - 'A' + 1;
            }

            // horner rule
            res = (res * z + val) % limit;
        }
        return res;
    }

    private void updateQ() {
        // find prime smaller than capacity
        for (int i = cap - 1; i >= 2; i--) {
            if (isPrime(i)) {
                q = i;
                return;
            }
        }
        q = 2;
    }

    public long getCols() {
        return cols;
    }
    public int size() {
        return size;
    }
}