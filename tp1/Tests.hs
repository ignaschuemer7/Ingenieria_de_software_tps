import Palet
import Route
import Stack
import Truck
import Control.Exception
import System.IO.Unsafe
import Control.Monad.RWS (MonadState(put))

-- Función para capturar excepciones en pruebas de error
testF :: Show a => a -> Bool
testF action = unsafePerformIO $ do
    result <- tryJust isException (evaluate action)
    return $ case result of
        Left _ -> True
        Right _ -> False
    where
        isException :: SomeException -> Maybe ()
        isException _ = Just ()


-- Definición de datos de prueba

-- Palets
p1 = newP "Buenos Aires" 3
p2 = newP "Rosario" 4
p3 = newP "Córdoba" 5
p4 = newP "Mendoza" 2


p5 = newP "Madrid" 2
p6 = newP "Barcelona" 4
p7 = newP "Valencia" 3

p8 = newP "Madrid" 7

p9 = newP "Barcelona" 11


-- Routes
r1 = newR ["Buenos Aires", "Rosario", "Córdoba", "Mendoza"]
r2 = newR ["Madrid", "Barcelona", "Valencia"]


-- Stacks
s1 = newS 5
s2 = stackS s1 p1
s3 = stackS s2 p2
s4 = stackS s3 p3
s5 = stackS s4 p4


-- Trucks
t1 = newT 5 7 r1

t2 = newT 3 4 r2

emptyTruck = newT 0 0 r1
flatTruck = newT 10 0 r1


-- Lista de pruebas
tests =
    [ 
      ----- Pruebas de Palet -----
      testF (newP "Buenos Aires" (-1)), -- Debería fallar, el peso no puede ser negativo
      not (testF (newP "Buenos Aires" 1)), -- No debería fallar

      ----- Pruebas de Route -----
      not (testF (newR [])), -- No debería fallar, una ruta puede ser vacía
      inOrderR r1 "Buenos Aires" "Córdoba",
      not (inOrderR r1 "Córdoba" "Buenos Aires"),

      ----- Pruebas de Stack -----
      freeCellsS s1 == 5,
      freeCellsS s2 == 4,
      freeCellsS s5 == 1,
      netS s5 == 14,
      holdsS s1 p1 r1,
      not (holdsS s2 p3 r1),

      ----- Pruebas de Truck -----
      testF (newT (-1) 5 r1), -- Debería fallar, la cantidad de bahías no puede ser negativa
      testF (newT 5 (-1) r1), -- Debería fallar, la altura de las bahías no puede ser negativa
      not (testF (newT 0 0 r1)), -- No debería fallar, un camión puede tener 0 bahías y 0 altura
      netT (loadT emptyTruck p1) == 0, -- No es posible cargar un palet en un camión sin bahías
      netT (loadT flatTruck p1) == 0, -- No es posible cargar un palet en un camión con bahías planas (sin altura)

      freeCellsT t2 == 12,
      netT t2 == 0,

      freeCellsT (loadT t2 p7) == 11,
      netT (loadT t2 p7) == 3,

      freeCellsT (loadT t1 p1) == 34,
      freeCellsT (loadT (loadT t1 p1) p2) == 33,
      netT (loadT (loadT (loadT t1 p1) p2) p3) == 12,
      freeCellsT (unloadT (loadT (loadT (loadT t1 p1) p2) p3) "Rosario") == 33,
      freeCellsT (unloadT (unloadT (loadT (loadT (loadT t1 p1) p2) p3) "Rosario") "Buenos Aires") == 34,


      ----- Carga de múltiples palets -----

      -- p7 + p7 + p6 + p7 + p6 = 5 cargas -> celdas libres = 12 (capacidad) - 5 (cargas) = 7
      freeCellsT (loadT (loadT (loadT (loadT (loadT t2 p7) p7) p6) p7) p6) == 7,
      -- 3 + 3 + 4 + 3 + 4 = 17 toneladas
      netT (loadT (loadT (loadT (loadT (loadT t2 p7) p7) p6) p7) p6) == 17,

      -- p7 + p7 + p6 + p7 + p6 + p5 + p5 = 7 cargas -> celdas libres = 12 (capacidad) - 7 (cargas) = 5
      freeCellsT (loadT (loadT (loadT (loadT (loadT (loadT (loadT t2 p7) p7) p6) p7) p6) p5) p5) == 5,

      -- p7 + p7 + p6 + p7 + p6 + p5 + p5 + p6 = 7 cargas ya que el último palet p6 no puede ser cargado -> celdas libres = 12 - 7 = 5
      freeCellsT (loadT (loadT (loadT (loadT (loadT (loadT (loadT (loadT t2 p7) p7) p6) p7) p6) p5) p5) p6) == 5,


      ----- Descarga en orden de ruta -----

      -- p7 + p7 + p6 + p7 + p6 + p5 - p5 = 5 cargas -> celdas libres = 12 - 5 = 7
      freeCellsT (unloadT (loadT (loadT (loadT (loadT (loadT (loadT (loadT t2 p7) p7) p6) p7) p6) p5) p5) "Madrid") == 7,

      -- p7 + p7 + p6 + p7 + p6 + p5 + p5 = 7 cargas - p5 - p5 - p6 - p6 = 3 cargas -> celdas libres = 12 - 3 = 9
      freeCellsT (unloadT (unloadT (loadT (loadT (loadT (loadT (loadT (loadT (loadT t2 p7) p7) p6) p7) p6) p5) p5) "Madrid") "Barcelona") == 9,
      -- 3 * p7 = 3 * 3 = 9 toneladas
      netT (unloadT (unloadT (loadT (loadT (loadT (loadT (loadT (loadT (loadT t2 p7) p7) p6) p7) p6) p5) p5) "Madrid") "Barcelona") == 9,

      -- se cargan y se descargan todos los palets -> celdas libres = 12 = capacidad del camión
      freeCellsT (unloadT (unloadT (unloadT (loadT (loadT (loadT (loadT (loadT (loadT (loadT t2 p7) p7) p6) p7) p6) p5) p5) "Madrid") "Barcelona") "Valencia") == 12,

      -- Intento de cargar un palet que excede el peso permitido (10 toneladas)
      netT (loadT t2 p9) == 0,

      -- Peso Truck vacío + peso de la carga = peso del camión con una carga
      netT t2 + netP p7 == netT (loadT t2 p7)

    ]

-- Evaluación de los tests
main :: IO ()
main = do
    let results = zip [1..] tests
    mapM_ (\(n, r) -> putStrLn $ "Test " ++ show n ++ ": " ++ if r then "OK" else "FAILED") results
    putStrLn $ "All tests passed: " ++ show (and tests)
    return ()