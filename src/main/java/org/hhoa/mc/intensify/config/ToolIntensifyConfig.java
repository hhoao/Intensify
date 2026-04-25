package org.hhoa.mc.intensify.config;

import com.google.common.base.Preconditions;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.hhoa.mc.intensify.util.TypeChecker;

public class ToolIntensifyConfig {
    private String name;
    private boolean enable;
    private List<AttributeConfig> attributes = new ArrayList<>();

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public List<AttributeConfig> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeConfig> attributes) {
        this.attributes = attributes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static class AttributeConfig {
        private Attribute type;
        private EnengConfig eneng;
        private List<GrowConfig> grows = new ArrayList<>();

        public Attribute getType() {
            return type;
        }

        public void setType(Attribute type) {
            this.type = type;
        }

        public EnengConfig getEneng() {
            return eneng;
        }

        public void setEneng(EnengConfig eneng) {
            this.eneng = eneng;
        }

        public List<GrowConfig> getGrows() {
            return grows;
        }

        public void setGrows(List<GrowConfig> grows) {
            this.grows = grows;
        }
    }

    public static class EnengConfig {
        private boolean enable;
        private List<Double> value;

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public List<Double> getValue() {
            return value;
        }

        public void setValue(List<Double> value) {
            Preconditions.checkArgument(!value.isEmpty() && value.size() <= 2);
            TypeChecker.checkAllElementsOfType(value, Double.class);
            this.value = value;
        }
    }

    public static class GrowConfig {
        private GrowTypeEnum type;
        private Range<Integer> range;
        private Double value;
        private int speed = 1;

        public GrowTypeEnum getType() {
            return type;
        }

        public void setType(GrowTypeEnum type) {
            this.type = type;
        }

        public Range<Integer> getRange() {
            return range;
        }

        public void setRange(List<Integer> rangeList) {
            int lower = rangeList.get(0);
            int upper = rangeList.get(1);
            if (upper == -1) {
                upper = Integer.MAX_VALUE;
            }
            this.range = Range.range(lower, BoundType.CLOSED, upper, BoundType.CLOSED);
        }

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }

        public int getSpeed() {
            return speed;
        }

        public void setSpeed(int speed) {
            this.speed = speed;
        }
    }

    public enum GrowTypeEnum {
        FIXED,
        PROPORTIONAL
    }
}
