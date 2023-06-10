circuit StridePrefetcher :
  module StridePrefetcher :
    input clock : Clock
    input reset : UInt<1>
    output io : { flip pc : UInt<32>, flip address : UInt<32>, prefetch_address : UInt<32>, prefetch_valid : UInt<1>}

    reg count : UInt<32>, clock with :
      reset => (reset, UInt<32>("h0")) @[StridePrefetcher.scala 27:22]
    node _T = geq(count, UInt<11>("h400")) @[StridePrefetcher.scala 28:14]
    when _T : @[StridePrefetcher.scala 28:25]
      count <= UInt<1>("h0") @[StridePrefetcher.scala 29:11]
    wire data_in : { PCS : UInt<32>, ADS : UInt<32>, PDS : UInt<32>} @[StridePrefetcher.scala 32:21]
    data_in.ADS <= io.address @[StridePrefetcher.scala 33:15]
    data_in.PCS <= io.pc @[StridePrefetcher.scala 34:15]
    reg file : { PCS : UInt<32>, ADS : UInt<32>, PDS : UInt<32>}[1024], clock with :
      reset => (UInt<1>("h0"), file) @[StridePrefetcher.scala 36:17]
    node _T_1 = gt(count, UInt<1>("h0")) @[StridePrefetcher.scala 38:14]
    when _T_1 : @[StridePrefetcher.scala 38:21]
      node _data_in_PDS_T = sub(count, UInt<1>("h1")) @[StridePrefetcher.scala 39:44]
      node _data_in_PDS_T_1 = tail(_data_in_PDS_T, 1) @[StridePrefetcher.scala 39:44]
      node _data_in_PDS_T_2 = bits(_data_in_PDS_T_1, 9, 0)
      node _data_in_PDS_T_3 = sub(data_in.ADS, file[_data_in_PDS_T_2].ADS) @[StridePrefetcher.scala 39:32]
      node _data_in_PDS_T_4 = tail(_data_in_PDS_T_3, 1) @[StridePrefetcher.scala 39:32]
      data_in.PDS <= _data_in_PDS_T_4 @[StridePrefetcher.scala 39:17]
    else :
      data_in.PDS <= UInt<3>("h4") @[StridePrefetcher.scala 41:17]
    node _T_2 = bits(count, 9, 0)
    file[_T_2] <= data_in @[StridePrefetcher.scala 44:15]
    node _T_3 = gt(count, UInt<1>("h0")) @[StridePrefetcher.scala 46:14]
    when _T_3 : @[StridePrefetcher.scala 46:21]
      node _T_4 = sub(count, UInt<1>("h1")) @[StridePrefetcher.scala 47:36]
      node _T_5 = tail(_T_4, 1) @[StridePrefetcher.scala 47:36]
      node _T_6 = bits(_T_5, 9, 0)
      node _T_7 = eq(data_in.PDS, file[_T_6].PDS) @[StridePrefetcher.scala 47:22]
      when _T_7 : @[StridePrefetcher.scala 47:47]
        node _io_prefetch_address_T = add(data_in.ADS, data_in.PDS) @[StridePrefetcher.scala 48:42]
        node _io_prefetch_address_T_1 = tail(_io_prefetch_address_T, 1) @[StridePrefetcher.scala 48:42]
        io.prefetch_address <= _io_prefetch_address_T_1 @[StridePrefetcher.scala 48:27]
        io.prefetch_valid <= UInt<1>("h1") @[StridePrefetcher.scala 49:25]
      else :
        node _io_prefetch_address_T_2 = add(data_in.ADS, data_in.PDS) @[StridePrefetcher.scala 51:42]
        node _io_prefetch_address_T_3 = tail(_io_prefetch_address_T_2, 1) @[StridePrefetcher.scala 51:42]
        io.prefetch_address <= _io_prefetch_address_T_3 @[StridePrefetcher.scala 51:27]
        io.prefetch_valid <= UInt<1>("h0") @[StridePrefetcher.scala 52:25]
    else :
      io.prefetch_address <= UInt<3>("h4") @[StridePrefetcher.scala 55:25]
      io.prefetch_valid <= UInt<1>("h1") @[StridePrefetcher.scala 56:23]
    node _count_T = add(count, UInt<1>("h1")) @[StridePrefetcher.scala 58:18]
    node _count_T_1 = tail(_count_T, 1) @[StridePrefetcher.scala 58:18]
    count <= _count_T_1 @[StridePrefetcher.scala 58:9]

