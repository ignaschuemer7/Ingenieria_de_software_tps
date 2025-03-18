import Palet
import Route
import Stack
import Truck
import Control.Exception
import System.IO.Unsafe

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

p5 = newP "Madrid" 2
p6 = newP "Barcelona" 4
p7 = newP "Valencia" 3

p8 = newP "Madrid" 7


-- Lista de pruebas
pruebas = 
    [ -- Pruebas de Route
      not (testF (newR [])),  -- No debería fallar (test 1)
      inOrderR r1 "Buenos Aires" "Córdoba" == True, -- test 2
      inOrderR r1 "Córdoba" "Buenos Aires" == False, -- test 3

      -- Pruebas de Stack
      freeCellsS s1 == 5, -- test 4
      freeCellsS s2 == 4, -- test 5
      freeCellsS s5 == 1, -- test 6
      netS s5 == 14, -- test 7
      holdsS s1 p1 r1 == True, -- test 8
      holdsS s2 p3 r1 == False, -- test 9

      -- Pruebas de Truck
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
      freeCellsT (loadT (loadT (loadT (loadT (loadT (loadT (loadT (loadT t2 p7) p7) p6) p7) p6) p5) p5) p8) == 4 -- test 22
    ]

-- Evaluación de los tests
main :: IO ()
main = do
    let resultados = zip [1..] pruebas
    mapM_ (\(n, r) -> putStrLn $ "Test " ++ show n ++ ": " ++ (if r then "OK" else "FALLÓ")) resultados