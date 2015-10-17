package org.webbuilder.utils.storage.instance.parser;

import org.webbuilder.utils.storage.StorageParser;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Created by 浩 on 2015-09-08 0008.
 */
public class KryoStorageParser extends StorageParser {
    @Override
    public <V> byte[] serialize(String key, V obj) {
        Kryo kryo = new Kryo();
        try (Output output = new Output(4096)) {
            kryo.writeObject(output, obj);
            return output.toBytes();
        }
    }

    @Override
    public <V> V deserialize(String key, byte[] data) {
        Kryo kryo = new Kryo();
        try (Input input = new Input(data)) {
            return (V) kryo.readObject(input, getType());
        }
    }

}
