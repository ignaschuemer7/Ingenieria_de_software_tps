
module Truck ( Truck, newT, freeCellsT, loadT, unloadT, netT )
  where

import Palet
import Stack
import Route

data Truck = Tru [ Stack ] Route deriving (Eq, Show)

newT :: Int -> Int -> Route -> Truck  -- construye un camion según una cantidad de bahias, la altura de las mismas y una ruta
newT amountBays height r | amountBays >= 0 && height >= 0 = Tru (replicate amountBays (newS height)) r
                         | otherwise = error "The amount of bays and the height must not be negative"

freeCellsT :: Truck -> Int            -- responde la celdas disponibles en el camion
freeCellsT (Tru bays _) = sum [freeCellsS s | s <- bays]

loadT :: Truck -> Palet -> Truck      -- carga un palet en el camion (una bahia no tolera apilar mas de 10 toneladas)
loadT (Tru bays r) p | null availableBays = Tru bays r -- no hay ningun stack o bahía en el que se puede cargar un palet. Se devuelve el camion sin cambios.
                     | otherwise = Tru (updateBays bays (head availableBays) p) r -- se carga el palet en la primera bahía disponible
                       where 
                        availableBays = getIndices bays p r


getIndices :: [Stack] -> Palet -> Route -> [Int] -- responde los indices de las bahias en las que se puede cargar un palet
getIndices stackList p r = [n | n <- [0..(length stackList -1)], holdsS (stackList !! n) p r, netS (stackList !! n) + netP p <= 10]

updateBays :: [Stack] -> Int -> Palet -> [Stack] -- actualiza las bahias del camion con el palet cargado
updateBays stackList index p = take index stackList ++ [stackS (stackList !! index) p] ++ drop (index + 1) stackList


unloadT :: Truck -> String -> Truck   -- responde un camion al que se le han descargado los paletes que podían descargarse en la ciudad
unloadT (Tru bays r) city = Tru ([popS s city | s <- bays]) r

netT :: Truck -> Int                  -- responde el peso neto en toneladas de los paletes en el camion
netT (Tru bays _) = sum [netS s | s <- bays]