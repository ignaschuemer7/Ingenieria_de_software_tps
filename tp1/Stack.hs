module Stack ( Stack, newS, freeCellsS, stackS, netS, holdsS, popS )
  where

import Palet
import Route

data Stack = Sta [ Palet ] Int deriving (Eq, Show)

newS :: Int -> Stack                      -- construye una Pila con la capacidad indicada 
newS = Sta [] 

freeCellsS :: Stack -> Int                -- responde la celdas disponibles en la pila
freeCellsS (Sta palets capacity) = capacity - length palets

stackS :: Stack -> Palet -> Stack         -- apila el palet indicado en la pila
stackS (Sta palets capacity) p | freeCellsS (Sta palets capacity) == 0 = Sta palets capacity
                               | otherwise = Sta (p : palets) capacity

netS :: Stack -> Int                      -- responde el peso neto de los paletes en la pila
netS (Sta palets _) = sum [netP p | p <- palets]

holdsS :: Stack -> Palet -> Route -> Bool -- indica si la pila puede aceptar el palet considerando las ciudades en la ruta
holdsS (Sta [] _) p r = inRouteR r (destinationP p)
holdsS (Sta palets _) p r = inOrderR r (destinationP p) (destinationP (head palets))

popS :: Stack -> String -> Stack          -- quita del tope los paletes con destino en la ciudad indicada
popS (Sta [] capacity) _ = Sta [] capacity -- si no hay palets devolvemos lo mismo (vacio)
popS (Sta (p:ps) capacity) city | destinationP p  /= city = Sta (p:ps) capacity -- condicion de corte si la ciudad destino no es la indicada
                                | otherwise = popS (Sta ps capacity) city -- se elimina el elemento de arriba del stack y se llama recursivamente 