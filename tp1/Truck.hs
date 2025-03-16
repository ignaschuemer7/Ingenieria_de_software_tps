
module Truck ( Truck, newT, freeCellsT) -- loadT, unloadT, netT )
  where

import Palet
import Stack
import Route

import Data.List (findIndices)

data Truck = Tru [ Stack ] Route deriving (Eq, Show)

newT :: Int -> Int -> Route -> Truck  -- construye un camion según una cantidad de bahias, la altura de las mismas y una ruta
newT cant_bahias altura r = Tru (replicate cant_bahias (newS altura)) r -- podemos hacerlo con recursion

freeCellsT :: Truck -> Int            -- responde la celdas disponibles en el camion
freeCellsT (Tru bahias _) = sum [freeCellsS s | s <- bahias] 

loadT :: Truck -> Palet -> Truck      -- carga un palet en el camion
loadT (Tru bahias r) p | null [s | s <- bahias, holdsS s, (netS s) + (netP p) <= 10] = Tru bahias r -- no hay ningun stack en el que se puede cargar un palet
                       | otherwise = Tru (stackS (head [s | s <- bahias, holdsS s, (netS s) + (netP p) <= 10]) p) r
-- for cada bahia llamar a holdsS, si devuelve True en alguna llamamos a stackS


filterFunction :: [Stack] -> [Stack]
filterFunction stack_list = foldl holdsS (newS 0) stack_list


-- filterCity :: [ Stack ] -> String -> [ Stack ] -- devuelve 
-- filterCity stack_list ciudad = findIndices (tail  == ciudad) stack_list




-- unloadT :: Truck -> String -> Truck   -- responde un camion al que se le han descargado los paletes que podían descargarse en la ciudad
-- una vez que se hace el unloadT en una ciudad hay que borrar la ciudad de la Route (drop 1 list_ciudades)


netT :: Truck -> Int                  -- responde el peso neto en toneladas de los paletes en el camion
netT (Tru bahias _) = sum [netS s | s <- bahias]