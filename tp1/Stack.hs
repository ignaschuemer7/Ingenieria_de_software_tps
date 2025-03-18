module Stack ( Stack, newS, freeCellsS, stackS, netS, holdsS, popS )
  where

import Palet
import Route

data Stack = Sta [ Palet ] Int deriving (Eq, Show)

newS :: Int -> Stack                      -- construye una Pila con la capacidad indicada 
newS capacidad = Sta [] capacidad 

freeCellsS :: Stack -> Int                -- responde la celdas disponibles en la pila
freeCellsS (Sta palets capacidad) = capacidad - length palets

stackS :: Stack -> Palet -> Stack         -- apila el palet indicado en la pila
stackS (Sta palets capacidad) p | freeCellsS (Sta palets capacidad) == 0 = Sta palets capacidad
                                | otherwise = Sta (p : palets) capacidad

netS :: Stack -> Int                      -- responde el peso neto de los paletes en la pila
netS (Sta palets _) = sum [netP p | p <- palets]

holdsS :: Stack -> Palet -> Route -> Bool -- indica si la pila puede aceptar el palet considerando las ciudades en la ruta
holdsS (Sta [] _) p r = inRouteR r (destinationP p)
holdsS (Sta palets _) p r = inOrderR r (destinationP p) (destinationP (head palets))

popS :: Stack -> String -> Stack          -- quita del tope los paletes con destino en la ciudad indicada
popS (Sta [] capacidad) _ = Sta [] capacidad -- si no hay palets devolvemos el mismo (vacio)
popS (Sta (p:ps) capacidad) ciudad | destinationP p  /= ciudad = Sta (p:ps) capacidad -- condicion de corte si la ciudad destino no es la indicada
                                   | otherwise = popS (Sta ps capacidad) ciudad -- eliminamos el elemento de arriba del stack 