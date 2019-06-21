package tec.codeexecutor;

import java.util.Objects;

public class Var {

    private String name;
    private Object value;
    private String type;

    public Var(String name, Object value, String type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public void setValue(Object value, String type) {
        if (this.type.equals(type)) {
            this.value = value;
        }
    }

    @Override
    public String toString() {
        return "Var{" +
                "name='" + name + '\'' +
                ", value=" + value +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Var var = (Var) o;
        return name.equals(var.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
