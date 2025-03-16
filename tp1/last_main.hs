module Main where

import Palet
import Route
import Stack

-- Función principal para testear todas las funciones
main :: IO ()
main = do
    -- Test Palet
    let pal1 = newP "Buenos Aires" 10  -- Crear un palet
    let pal2 = newP "Cordoba" 5
    let pal3 = newP "Rosario" 8
    putStrLn "Testing Palet module:"
    putStrLn $ "Palet 1: " ++ show pal1
    putStrLn $ "Palet 2: " ++ show pal2
    putStrLn $ "Palet 3: " ++ show pal3
    putStrLn $ "Destination of Palet 1: " ++ destinationP pal1  -- Ciudad destino
    putStrLn $ "Net weight of Palet 1: " ++ show (netP pal1)  -- Peso neto
    putStrLn $ "Destination of Palet 2: " ++ destinationP pal2  -- Ciudad destino
    putStrLn $ "Net weight of Palet 3: " ++ show (netP pal3)  -- Peso neto
    putStrLn ""

    -- Test Route
    let route = newR ["Buenos Aires", "Cordoba", "Rosario"]
    putStrLn "Testing Route module:"
    putStrLn $ "Route: " ++ show route
    putStrLn $ "Is 'Buenos Aires' before 'Cordoba'? " ++ show (inOrderR route "Buenos Aires" "Cordoba")
    putStrLn $ "Is 'Cordoba' before 'Buenos Aires'? " ++ show (inOrderR route "Cordoba" "Buenos Aires")
    putStrLn ""

    -- Test Stack
    let stack1 = newS 5  -- Crear una pila con capacidad 5
    putStrLn "Testing Stack module:"
    putStrLn $ "Initial Stack: " ++ show stack1
    let stack2 = stackS stack1 pal1  -- Apilar un palet
    putStrLn $ "Stack after adding Palet 1: " ++ show stack2
    putStrLn $ "Free cells in stack: " ++ show (freeCellsS stack2)
    
    let stack3 = stackS stack2 pal2  -- Apilar otro palet
    putStrLn $ "Stack after adding Palet 2: " ++ show stack3
    putStrLn $ "Free cells in stack: " ++ show (freeCellsS stack3)

    -- Test holdsS
    let routeForStack = newR ["Buenos Aires", "Cordoba", "Rosario"]
    putStrLn $ "STACK 3 -- " ++ show stack3
    putStrLn $ "Can the stack hold Palet 3 given the route? " ++ show (holdsS stack3 pal3 routeForStack)
    putStrLn $ "Can the stack hold Palet 1 given the route? " ++ show (holdsS stack3 pal1 routeForStack)

    -- Test popS
    let stack4 = popS stack3 "Cordoba"  -- Quitar el palet con destino en Córdoba
    putStrLn $ "Stack after popping Palet with destination 'Cordoba': " ++ show stack4
    putStrLn $ "Free cells in stack after pop: " ++ show (freeCellsS stack4)

