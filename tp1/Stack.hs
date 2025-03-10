module Stack ( Stack, newS, freeCellsS, stackS, netS, holdsS, popS )
  where

import Palet
import Route

data Stack = Sta [ Palet ] Int deriving (Eq, Show)

newS :: Int -> Stack                      -- construye una Pila con la capacidad indicada 
newS capacidad = Sta [] capacidad 

freeCellsS :: Stack -> Int                -- responde la celdas disponibles en la pila
freeCellsS (Sta palets capacidad) = capacidad - length(palets)

stackS :: Stack -> Palet -> Stack         -- apila el palet indicado en la pila
stackS (Sta palets capacidad) p | freeCellsS (Sta palets capacidad) == 0 = (Sta palets capacidad)
                                | otherwise = Sta (p : palets) capacidad

-- Preguntar si hay que crear un nuevo Stack o se puede devolver el que ya está creado?
-- Por qué no se puede llamar a esta función como:
-- ghci> pila = newS 5
-- ghci> pal = newP "La Plata" 14
-- ghci> pila = stackS pila pal
-- ghci> freeCellsS pila -> acá se rompe


netS :: Stack -> Int                      -- responde el peso neto de los paletes en la pila
netS (Sta palets _) = sum [netP p | p <- palets]

holdsS :: Stack -> Palet -> Route -> Bool -- indica si la pila puede aceptar el palet considerando las ciudades en la ruta
holdsS (Sta palets _) p r | null palets = True
                          | otherwise = inOrderR r (destinationP p) (destinationP (head palets))

popS :: Stack -> String -> Stack          -- quita del tope los paletes con destino en la ciudad indicada
popS (Sta palets capacidad) ciudad | null palets = Sta palets capacidad -- si no hay palets devolvemos el mismo
                                   | destinationP( head palets ) /= ciudad = Sta palets capacidad -- condicion de corte si la ciudad destino no es la indicada
                                   | otherwise = popS (Sta (tail palets) capacidad) ciudad -- eliminamos el elemento de arriba del stack 