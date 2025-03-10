
module Truck ( Truck, newT, freeCellsT) -- loadT, unloadT, netT )
  where

import Palet
import Stack
import Route

data Truck = Tru [ Stack ] Route deriving (Eq, Show)

newT :: Int -> Int -> Route -> Truck  -- construye un camion según una cantidad de bahias, la altura de las mismas y una ruta
newT cant_bahias altura r = Tru (replicate cant_bahias (newS altura)) r -- podemos hacerlo con recursion

freeCellsT :: Truck -> Int            -- responde la celdas disponibles en el camion
freeCellsT (Tru bahias _) = sum [freeCellsS s | s <- bahias] 

loadT :: Truck -> Palet -> Truck      -- carga un palet en el camion
loadT (Tru bahias r) p | null [s | s <- bahias, holdsS s] = Tru bahias r -- no hay ningun stack en el que se puede cargar un palet
                       | otherwise = Tru (stackS (head [s | s <- bahias, holdsS s]) p) r
-- for cada bahia llamar a holdsS, si devuelve True en alguna llamamos a stackS

-- unloadT :: Truck -> String -> Truck   -- responde un camion al que se le han descargado los paletes que podían descargarse en la ciudad

netT :: Truck -> Int                  -- responde el peso neto en toneladas de los paletes en el camion
netT (Tru bahias _) = sum [netS s | s <- bahias]