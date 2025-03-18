
module Truck ( Truck, newT, freeCellsT, loadT, unloadT, netT )
  where

import Palet
import Stack
import Route

data Truck = Tru [ Stack ] Route deriving (Eq, Show)

newT :: Int -> Int -> Route -> Truck  -- construye un camion según una cantidad de bahias, la altura de las mismas y una ruta
newT cantBahias altura r | cantBahias >= 0 && altura >= 0 = Tru (replicate cantBahias (newS altura)) r
                         | otherwise = error "La cantidad de bahias y la altura deben ser mayores a 0" 

freeCellsT :: Truck -> Int            -- responde la celdas disponibles en el camion
freeCellsT (Tru bahias _) = sum [freeCellsS s | s <- bahias]

loadT :: Truck -> Palet -> Truck      -- carga un palet en el camion (una bahia no tolera apilar mas de 10 toneladas)
loadT (Tru bahias r) p | null posiblesBahias = Tru bahias r -- no hay ningun stack en el que se puede cargar un palet
                       | otherwise = Tru (updateBahias bahias (head posiblesBahias) p) r
                       where 
                        posiblesBahias = getIndices bahias p r


getIndices :: [Stack] -> Palet -> Route -> [Int]
getIndices stackList p r = [n | n <- [0..(length stackList -1)], holdsS (stackList !! n) p r, netS (stackList !! n) + netP p <= 10]


updateBahias :: [Stack] -> Int -> Palet -> [Stack]
updateBahias stackList index p = take index stackList ++ [stackS (stackList !! index) p] ++ drop (index + 1) stackList

unloadT :: Truck -> String -> Truck   -- responde un camion al que se le han descargado los paletes que podían descargarse en la ciudad
unloadT (Tru bahias r) ciudad = Tru ([popS s ciudad | s <- bahias]) r
-- una vez que se hace el unloadT en una ciudad hay que borrar la ciudad de la Route (drop 1 list_ciudades)


netT :: Truck -> Int                  -- responde el peso neto en toneladas de los paletes en el camion
netT (Tru bahias _) = sum [netS s | s <- bahias]