
module Truck ( Truck, newT, freeCellsT, loadT, unloadT, netT )
  where

import Palet
import Stack
import Route

data Truck = Tru [ Stack ] Route deriving (Eq, Show)

newT :: Int -> Int -> Route -> Truck  -- construye un camion según una cantidad de bahias, la altura de las mismas y una ruta
newT cant_bahias altura r = Tru (replicate cant_bahias (newS altura)) r -- podemos hacerlo con recursion

freeCellsT :: Truck -> Int            -- responde la celdas disponibles en el camion
freeCellsT (Tru bahias _) = sum [freeCellsS s | s <- bahias]

loadT :: Truck -> Palet -> Truck      -- carga un palet en el camion (una bahia no tolera apilar mas de 10 toneladas)
loadT (Tru bahias r) p | null (getIndices bahias p r) = Tru bahias r -- no hay ningun stack en el que se puede cargar un palet
                      --  | not (null (filterCity bahias (getIndices bahias p r) (destinationP p))) = Tru (updateBahias bahias (head (filterCity bahias (getIndices bahias p r) (destinationP p))) p)  r
                       | otherwise = Tru (updateBahias bahias (head (getIndices bahias p r)) p) r


getIndices :: [Stack] -> Palet -> Route -> [Int]
getIndices stack_list p r = [n | n <- [0..(length stack_list -1)], holdsS (stack_list !! n) p r, netS (stack_list !! n) + netP p <= 10]

-- filterCity :: [ Stack ] -> [Int] -> String -> [Int] 
-- filterCity stack_list indices ciudad = [idx | idx <- indices, destinationP (head (stack_list !! idx)) == ciudad ]

updateBahias :: [Stack] -> Int -> Palet -> [Stack]
updateBahias stack_list index p = take index stack_list ++ [stackS (stack_list !! index) p] ++ drop (index + 1) stack_list

unloadT :: Truck -> String -> Truck   -- responde un camion al que se le han descargado los paletes que podían descargarse en la ciudad
unloadT (Tru bahias r) ciudad = Tru ([popS s ciudad | s <- bahias]) r
-- una vez que se hace el unloadT en una ciudad hay que borrar la ciudad de la Route (drop 1 list_ciudades)


netT :: Truck -> Int                  -- responde el peso neto en toneladas de los paletes en el camion
netT (Tru bahias _) = sum [netS s | s <- bahias]