package java.util;
import java.util.function.Consumer;
import java.util.HashMap.KeySpliterator;

public class KeySpliteratorShuffler<K, V> {

    private final Iterator<K> iter;
    private final KeySpliterator<K, V> keySpliterator;

    public KeySpliteratorShuffler(KeySpliterator<K, V> ks) {
        this.keySpliterator = ks;

        final List<K> keys = new ArrayList<>();
        keySpliterator.original_forEachRemaining(key -> keys.add(key));

        List<K> res = edu.illinois.nondex.shuffling.ControlNondeterminism.shuffle(keys);

        iter = res.iterator();
    }

    public void forEachRemaining(Consumer<? super K> action) {
        iter.forEachRemaining(action);
    }
}