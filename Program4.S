set 0, 2
set 1, 1
set D[0], D[1]
set 0, D[0] + 1
set 1, (2 * D[1]) % 13
jumpt 2, D[0] < 100
halt 
