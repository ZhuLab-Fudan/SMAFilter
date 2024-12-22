package cn.edu.fudan.ISTBI.checker;

import cn.edu.fudan.ISTBI.entity.NumberPattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class NumericChecker
{
    final NumberPattern numberPattern;

    public NumericChecker(NumberPattern pattern)
    {
        this.numberPattern = pattern;
    }

    public boolean evaluate(double input)
    {
        boolean result = false;
        switch (numberPattern.getType())
        {
            case 0: // smaller than
                result = (input < numberPattern.getMax());
                break;
            case 1: // between
                result = (input > numberPattern.getMin()) && (input < numberPattern.getMax());
                break;
            case 2:
                result = (input > numberPattern.getMin());
                break;
            default:
        }
        return result;
    }

}
