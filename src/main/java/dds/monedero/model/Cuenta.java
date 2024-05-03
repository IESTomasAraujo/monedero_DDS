package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo = 0;
  private List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta() {
    saldo = 0;
  }

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }

  public void poner(double cuanto) {
    if (cuanto <= 0) {// Código duplicado
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");// Código duplicado
    }// Código duplicado

    if (getMovimientos().stream().filter(movimiento -> movimiento.isDeposito()).count() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }

    new Movimiento(LocalDate.now(), cuanto, true).agregateA(this);// Código duplicado
  }

  public void sacar(double cuanto) {
    if (cuanto <= 0) {// Código duplicado
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");// Código duplicado
    }// Código duplicado
    if (getSaldo() - cuanto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
    double montoExtraidoHoy = getMontoExtraidoA();
    double limite = 1000 - montoExtraidoHoy;
    if (cuanto > limite) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + limite);
    }
    new Movimiento(LocalDate.now(), cuanto, false).agregateA(this);// Código duplicado
  }

  // ANTES -> public void agregarMovimiento(LocalDate fecha, double cuanto, boolean esDeposito) {
    public void agregarMovimiento(Movimiento movimiento) { // CodeSmell->Long parameter List
                                                                                      // Nos falta la abstraccion de Movimiento
    //Movimiento movimiento = new Movimiento(fecha, cuanto, esDeposito);
    movimientos.add(movimiento);
  }

  public double getMontoExtraidoA() {// No es necesario el parámetro, se podría instanciar sin parametro y se calcularía con la fecha de hoy igualmente
    return getMovimientos().stream()
        .filter(movimiento -> !movimiento.isDeposito() && movimiento.getFecha().equals(LocalDate.now()))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }

}
