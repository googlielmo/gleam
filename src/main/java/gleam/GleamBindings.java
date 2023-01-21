package gleam;

import gleam.lang.*;
import gleam.util.Converter;
import gleam.util.MapAdapter;

import javax.script.Bindings;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static gleam.GleamScriptEngine.unwrap;
import static gleam.GleamScriptEngine.wrap;

public class GleamBindings extends Environment implements Bindings {
    final transient private MapAdapter<Symbol, Location, String, Object> assocAdapter =
        new MapAdapter<>(assoc,
            new SymbolStringConverter(),
            new LocationObjectConverter());

    public GleamBindings(Environment parent) {
        super(parent);
    }

    public GleamBindings(InputPort in, OutputPort out) {
        super(in, out);
    }

    public GleamBindings(Environment parent, Bindings bindings) {
        super(parent);
        MapAdapter<String, Object, Symbol, Location> assocAdapter =
            new MapAdapter<>(bindings,
                new SymbolStringConverter().inverseConverter(),
                new LocationObjectConverter().inverseConverter());
        assocAdapter.forEach(this::define);
    }

    @Override
    public Object put(String name, Object value) {
        return assocAdapter.put(name, value);
    }

    @Override
    public void putAll(Map<? extends String, ?> toMerge) {
        assocAdapter.putAll(toMerge);
    }

    @Override
    public boolean containsKey(Object key) {
        return assocAdapter.containsKey(key);
    }

    @Override
    public Object get(Object key) {
        return assocAdapter.get(key);
    }

    @Override
    public Object remove(Object key) {
        return assocAdapter.remove(key);
    }

    @Override
    public int size() {
        return assocAdapter.size();
    }

    @Override
    public boolean isEmpty() {
        return assocAdapter.isEmpty();
    }

    @Override
    public boolean containsValue(Object value) {
        return assocAdapter.containsValue(value);
    }

    @Override
    public void clear() {
        assocAdapter.clear();
    }

    @Override
    public Set<String> keySet() {
        return assocAdapter.keySet();
    }

    @Override
    public Collection<Object> values() {
        return assocAdapter.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return assocAdapter.entrySet();
    }

    private static class SymbolStringConverter implements Converter<Symbol, String> {

        @Override
        public String convert(Symbol value) {
            return value.toString();
        }

        @Override
        public Symbol invert(String value) {
            Objects.requireNonNull(value); // FIXME check "" as well?
            return Symbol.makeSymbol(value);
        }

        @Override
        public String convertAny(Object value) {
            return value.toString();
        }

        @Override
        public Symbol invertAny(Object value) {
            Objects.requireNonNull(value); // FIXME check "" as well?
            return Symbol.makeSymbol(value.toString());
        }
    }

    private class LocationObjectConverter implements Converter<Location, Object> {

        @Override
        public Object convert(Location value) {
            return value == null ? null : unwrap(value.get()); // FIXME nulls
        }

        @Override
        public Location invert(Object value) {
            Entity entity = wrap(value);
            return assoc.values().stream()
                .filter(location -> location.get().equals(entity))
                .findFirst()
                .orElse(null); // FIXME nulls
        }

        @Override
        public Object convertAny(Object value) {
            return convert((Location) value);
        }

        @Override
        public Location invertAny(Object value) {
            return invert(value);
        }
    }
}
