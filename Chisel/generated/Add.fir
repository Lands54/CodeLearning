circuit Add :
  module Add :
    input clock : Clock
    input reset : UInt<1>
    output io : { flip a : UInt<8>, flip b : UInt<8>, c : UInt<8>}

    reg reg : UInt<8>, clock with :
      reset => (reset, UInt<8>("h0")) @[Add.scala 20:20]
    node _reg_T = add(io.a, io.b) @[Add.scala 21:15]
    node _reg_T_1 = tail(_reg_T, 1) @[Add.scala 21:15]
    reg <= _reg_T_1 @[Add.scala 21:7]
    io.c <= reg @[Add.scala 23:8]

