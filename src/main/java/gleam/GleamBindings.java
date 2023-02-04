package gleam;

import gleam.lang.Entity;
import gleam.lang.Environment;
import gleam.lang.Location;
import gleam.lang.Symbol;
import gleam.util.Converter;
import gleam.util.MapAdapter;

import javax.script.Bindings;
import javax.script.SimpleBindings;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static gleam.GleamScriptEngine.unwrap;
import static gleam.GleamScriptEngine.wrap;

public class GleamBindings extends Environment implements Bindings
{
    private final SymbolStringConverter symbolStringConverter = new SymbolStringConverter();
    private final LocationObjectConverter locationObjectConverter = new LocationObjectConverter();
    final transient private MapAdapter<Symbol, Location, String, Object> assocAdapter =
            new MapAdapter<>(
                    assoc,
                    symbolStringConverter,
                    locationObjectConverter);

    public GleamBindings(Environment parent)
    {
        this(parent, new SimpleBindings());
    }

    public GleamBindings(Environment parent, Bindings bindings)
    {
        super(parent);
        this.putAll(bindings);
    }

    @Override
    public Object put(String name, Object value)
    {
        Object prev = null;
        if (assocAdapter.containsKey(name)) {
            prev = assocAdapter.get(name);
        }
        Symbol symbol = symbolStringConverter.invert(name);
        Entity entity = wrap(value);
        define(symbol, entity);
        return prev;
    }

    @Override
    public void putAll(Map<? extends String, ?> toMerge)
    {
        toMerge.forEach(this::put);
    }

    @Override
    public boolean containsKey(Object key)
    {
        return assocAdapter.containsKey(key);
    }

    @Override
    public Object get(Object key)
    {
        return assocAdapter.get(key);
    }

    @Override
    public Object remove(Object key)
    {
        return assocAdapter.remove(symbolStringConverter.invert((String) key));
    }

    @Override
    public int size()
    {
        return assocAdapter.size();
    }

    @Override
    public boolean isEmpty()
    {
        return assocAdapter.isEmpty();
    }

    @Override
    public boolean containsValue(Object value)
    {
        return assocAdapter.containsValue(value);
    }

    @Override
    public void clear()
    {
        assocAdapter.clear();
    }

    @Override
    public Set<String> keySet()
    {
        return assocAdapter.keySet();
    }

    @Override
    public Collection<Object> values()
    {
        return assocAdapter.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet()
    {
        return assocAdapter.entrySet();
    }

    private static class SymbolStringConverter implements Converter<Symbol, String>
    {

        @Override
        public String convert(Symbol value)
        {
            return value.toString();
        }

        @Override
        public Symbol invert(String value)
        {
            Objects.requireNonNull(value); // FIXME check "" as well?
            return Symbol.makeSymbol(value);
        }

        @Override
        public String convertAny(Object value)
        {
            return value.toString();
        }

        @Override
        public Symbol invertAny(Object value)
        {
            if (value.equals("")) { // implicit null check for value
                throw new IllegalArgumentException("value");
            }
            return Symbol.makeSymbol(value.toString());
        }
    }

    private class LocationObjectConverter implements Converter<Location, Object>
    {
        @Override

        public Object convert(Location value)
        {
            return Objects.requireNonNull(unwrap(value.get()));
        }

        @Override
        public Location invert(Object value)
        {
            Entity entity = wrap(value);
            return assoc.values()
                        .stream()
                        .filter(location -> location.get().equals(entity))
                        .findFirst()
                        .orElseThrow(NullPointerException::new);
        }

        @Override
        public Object convertAny(Object value)
        {
            return convert((Location) value);
        }

        @Override
        public Location invertAny(Object value)
        {
            return invert(value);
        }
    }
}