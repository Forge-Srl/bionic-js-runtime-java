package acceptance.toy1.components;

import acceptance.toy1.bjs.ToyComponent1BjsExport;

public class ToyComponent1 implements ToyComponent1BjsExport
{
    private String number1;
    private String number2;

    public static Double PI()
    {
        return Math.PI;
    }

    public static Long sum(Long number1, Long number2) {
        return number1 + number2;
    }

    public ToyComponent1(String number1, String number2)
    {
        this.number1 = number1;
        this.number2 = number2;
    }

    @Override
    public Long number1()
    {
        return Long.parseLong(number1);
    }

    @Override
    public void number1(Long value)
    {
        number1 = value.toString();
    }

    @Override
    public Long number2()
    {
        return Long.parseLong(number2);
    }

    @Override
    public void number2(Long value)
    {
        number2 = value.toString();
    }

    @Override
    public Long getSum(Long offset)
    {
        return offset + number1() + number2();
    }

    @Override
    public Long getToySum(ToyComponent1BjsExport toyComponent1)
    {
        return getSum(0L) + toyComponent1.getSum(0L);
    }
}
