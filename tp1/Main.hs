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
p1 = newP "Buenos Aires" 3
p2 = newP "Rosario" 4
p3 = newP "Córdoba" 5
p4 = newP "Mendoza" 2

r1 = newR ["Buenos Aires", "Rosario", "Córdoba", "Mendoza"]
r2 = newR ["Madrid", "Barcelona", "Valencia"]

s1 = newS 5
s2 = stackS s1 p1
s3 = stackS s2 p2
s4 = stackS s3 p3
s5 = stackS s4 p4

t1 = newT 5 7 r1

t2 = newT 3 4 r2

emptyTruck = newT 0 0 r1
flatTruck = newT 10 0 r1


p5 = newP "Madrid" 2
p6 = newP "Barcelona" 4
p7 = newP "Valencia" 3

p8 = newP "Madrid" 7

p9 = newP "Barcelona" 11


-- Lista de pruebas
pruebas =
    [ -- Pruebas de Route
      not (testF (newR [])),  -- No debería fallar (test 1)
      inOrderR r1 "Buenos Aires" "Córdoba", -- test 2
      not (inOrderR r1 "Córdoba" "Buenos Aires"), -- test 3

      -- Pruebas de Stack
      freeCellsS s1 == 5, -- test 4
      freeCellsS s2 == 4, -- test 5
      freeCellsS s5 == 1, -- test 6
      netS s5 == 14, -- test 7
      holdsS s1 p1 r1, -- test 8
      not (holdsS s2 p3 r1), -- test 9

      -- Pruebas de Truck
      testF (newT (-1) 5 r1), -- Debería fallar (test 10)
      testF (newT 5 (-1) r1), -- Debería fallar (test 11)
      not (testF (newT 0 0 r1)), -- No debería fallar (test 12)

      -- Check empty truck behavior
      netT (loadT emptyTruck p1) == 0, -- No es posible cargar un palet en un camión sin bahías
      -- Check flat truck behavior
      netT (loadT flatTruck p1) == 0, -- No es posible cargar un palet en un camión con bahías planas

      freeCellsT (loadT t1 p1) == 34, -- test 10
      freeCellsT (loadT (loadT t1 p1) p2) == 33, -- test 11
      netT (loadT (loadT (loadT t1 p1) p2) p3) == 12, -- test 12
      freeCellsT (unloadT (loadT (loadT (loadT t1 p1) p2) p3) "Rosario") == 33, -- test 13
      freeCellsT (unloadT (unloadT (loadT (loadT (loadT t1 p1) p2) p3) "Rosario") "Buenos Aires") == 34, -- test 14

      freeCellsT t2 == 12, -- test 15
      netT t2 == 0, -- test 16
      freeCellsT (loadT t2 p7) == 11, -- test 17
      netT (loadT t2 p7) == 3, -- test 18
      -- load p7, load p7, load p6, load p7, load p6
      freeCellsT (loadT (loadT (loadT (loadT (loadT t2 p7) p7) p6) p7) p6) == 7, -- test 19
      netT (loadT (loadT (loadT (loadT (loadT t2 p7) p7) p6) p7) p6) == 17, -- test 20
      -- load p7, load p7, load p6, load p7, load p6, load p5, load p5 (solo se puede cargar en la ultima bahia y de p5)
      freeCellsT (loadT (loadT (loadT (loadT (loadT (loadT (loadT t2 p7) p7) p6) p7) p6) p5) p5) == 5, -- test 21
      -- load p7, load p7, load p6, load p7, load p6, load p5, load p5, load p6 (p6 no se puede cargar)
      freeCellsT (loadT (loadT (loadT (loadT (loadT (loadT (loadT (loadT t2 p7) p7) p6) p7) p6) p5) p5) p6) == 5, -- test 22
      -- Unload the last sequence of palets in the route order
      freeCellsT (unloadT (loadT (loadT (loadT (loadT (loadT (loadT (loadT t2 p7) p7) p6) p7) p6) p5) p5) "Madrid") == 7, 
      freeCellsT (unloadT (unloadT (loadT (loadT (loadT (loadT (loadT (loadT (loadT t2 p7) p7) p6) p7) p6) p5) p5) "Madrid") "Barcelona") == 9,
      freeCellsT (unloadT (unloadT (unloadT (loadT (loadT (loadT (loadT (loadT (loadT (loadT t2 p7) p7) p6) p7) p6) p5) p5) "Madrid") "Barcelona") "Valencia") == 12, -- Si se descargan los palets en el orden de la ruta, se debería tener la misma cantidad de celdas libres que al principio
      
      -- Try to load a palet that exceeds the weight limit
      netT (loadT t2 p9) == 0 -- test 23
      
      
     

    ]

-- Evaluación de los tests
main :: IO ()
main = do
    let resultados = zip [1..] pruebas
    mapM_ (\(n, r) -> putStrLn $ "Test " ++ show n ++ ": " ++ (if r then "OK" else "FALLÓ")) resultados
    -- print the and of the tests to check if all of them passed
    putStrLn $ "All tests passed: " ++ show (and pruebas)
    return ()