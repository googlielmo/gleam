package gleam;

import gleam.lang.Entity;
import gleam.lang.Environment;
import gleam.lang.Location;
import gleam.lang.Symbol;
import gleam.util.Converter;
import gleam.util.MapAdapter;

import javax.script.Bindings;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static gleam.GleamScriptEngine.objectOf;
import static gleam.GleamScriptEngine.entityOf;

public class GleamBindings extends Environment implements Bindings
{
    private final SymbolStringConverter symbolStringConverter = new SymbolStringConverter();
    private final LocationObjectConverter locationObjectConverter = new LocationObjectConverter();
    final transient private BindingsAdapter assocAdapter;

    public GleamBindings(Environment parent)
    {
        this(parent, null);
    }

    public GleamBindings(Environment parent, Bindings bindings)
    {
        super(parent);
        assocAdapter = new BindingsAdapter(bindings == null
                                           ? new HashMap<>()
                                           : bindings);
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
    public boolean containsKey(Object key)
    {
        return assocAdapter.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value)
    {
        return assocAdapter.containsValue(value);
    }

    @Override
    public Object get(Object key)
    {
        return assocAdapter.get(key);
    }

    @Override
    public Object put(String key, Object value)
    {
        return assocAdapter.put(key, value);
    }

    @Override
    public Object remove(Object key)
    {
        return assocAdapter.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m)
    {
        assocAdapter.putAll(m);
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

    @Override
    public boolean remove(Object key, Object value)
    {
        return assocAdapter.remove(key, value);
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
            return Objects.requireNonNull(objectOf(value.get()));
        }

        @Override
        public Location invert(Object value)
        {
            Entity entity = entityOf(value);
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

    private class BindingsAdapter extends MapAdapter<Symbol, Location, String, Object>
    {
        private final Map<String, Object> bindings;

        public BindingsAdapter(Map<String, Object> bindings)
        {
            super(GleamBindings.this.assoc,
                  GleamBindings.this.symbolStringConverter,
                  GleamBindings.this.locationObjectConverter);

            for (Entry<String, Object> e : bindings.entrySet()) {
                define(symbolStringConverter.invert(e.getKey()),
                       entityOf(e.getValue()));
            }
            this.bindings = bindings;
        }

        @Override
        public Object put(String key, Object value)
        {
            bindings.put(key, value);
            return objectOf(define(symbolStringConverter.invert(key),
                                   entityOf(value)).get());
        }

        @Override
        public Object remove(Object key)
        {
            bindings.remove(key);
            return super.remove(key);
        }

        @Override
        public void putAll(Map<? extends String, ?> m)
        {
            m.forEach((k1, v1) -> define(symbolStringConverter.invert(k1), entityOf(v1)));
            bindings.putAll(m);
            super.putAll(m);
        }
    }
}
