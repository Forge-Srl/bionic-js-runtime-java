package acceptance.toy1.components;

import acceptance.toy1.bjs.ToyComponent1BjsExport;
import acceptance.toy1.bjs.ToyComponent2BjsExport;

public class ToyComponent2 extends ToyComponent1 implements ToyComponent2BjsExport
{
    public static Double PI2()
    {
        return Math.PI + 3;
    }

    public ToyComponent2(String number1, String number2)
    {
        super(number1, number2);
    }

    @Override
    public Long getSum(Long offset)
    {
        return super.getSum(offset) * 2;
    }

    @Override
    public Long getToySum(ToyComponent1BjsExport toyComponent1)
    {
        return getSum(1L) + toyComponent1.getSum(1L);
    }

    @Override
    public Long additionalMethod()
    {
        return 69420L;
    }
}
